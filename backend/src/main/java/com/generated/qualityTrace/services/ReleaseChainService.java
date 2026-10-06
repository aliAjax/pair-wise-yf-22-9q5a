package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.BatchReleaseStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.ReleaseSubmissionOutcome;
import com.generated.qualityTrace.constants.RoleCode;
import com.generated.qualityTrace.exceptions.BatchNotFoundException;
import com.generated.qualityTrace.exceptions.BizException;
import com.generated.qualityTrace.exceptions.ReleaseForbiddenException;
import com.generated.qualityTrace.exceptions.ReleaseStillBlockedException;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.ReleaseSubmission;
import com.generated.qualityTrace.repositories.BatchReleaseRepository;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.repositories.ReleaseSubmissionRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 放行链服务：把工单、批次、终检结论和不良记录接成一条放行链，维护批次放行结论。
 *
 * <p>规则：
 * 1. 只要还有未处置的严重不良（CRITICAL + OPEN），批次就停在待处置；
 * 2. 不良记录一变，已算出的放行结论立即作废（stale）并重算，已放行的也会被拉回待处置；
 * 3. 同一批次同一放行周期内，先到的提交生效，晚到的留在待复核；
 * 4. 待处置是粘性的：自动重算不会解除，只有质量经理能解除。
 */
@Service
public class ReleaseChainService {
  private static final Logger log = LoggerFactory.getLogger(ReleaseChainService.class);

  private final ProductBatchRepository batchRepo;
  private final QualityInspectionRepository inspectionRepo;
  private final DefectRecordRepository defectRepo;
  private final BatchReleaseRepository releaseRepo;
  private final ReleaseSubmissionRepository submissionRepo;
  /** 每批次一把锁：提交判先后、结论重算、待处置解除都在同一把锁内串行。 */
  private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

  public ReleaseChainService(ProductBatchRepository batchRepo,
                             QualityInspectionRepository inspectionRepo,
                             DefectRecordRepository defectRepo,
                             BatchReleaseRepository releaseRepo,
                             ReleaseSubmissionRepository submissionRepo) {
    this.batchRepo = batchRepo;
    this.inspectionRepo = inspectionRepo;
    this.defectRepo = defectRepo;
    this.releaseRepo = releaseRepo;
    this.submissionRepo = submissionRepo;
  }

  /** 读取当前放行结论；已作废（stale）或不存在时先重算。 */
  public BatchRelease getRelease(String batchNo) {
    synchronized (lockOf(batchNo)) {
      return getOrComputeLocked(batchNo);
    }
  }

  /** 放行提交：同一批次同一周期内先到生效（APPLIED），晚到留在待复核（PENDING_RECHECK）。 */
  public ReleaseSubmission submit(String batchNo, String actorId, String role, String note) {
    RoleCode roleCode = parseRole(role);
    if (RoleCode.AUDITOR == roleCode) {
      log.warn(LogTemplates.SUBMIT_REJECTED, batchNo, actorId, role, "auditor is read-only");
      throw new BizException(ErrorCodes.RBAC_DENIED, ErrorMessages.RBAC_DENIED + ": " + role, 403);
    }
    synchronized (lockOf(batchNo)) {
      BatchRelease release = getOrComputeLocked(batchNo);
      boolean applied = submissionRepo.tryReserveApplied(batchNo, release.cycle);
      ReleaseSubmission submission = new ReleaseSubmission(
          null, batchNo, release.cycle, actorId, roleCode.name(),
          applied ? ReleaseSubmissionOutcome.APPLIED.name()
                  : ReleaseSubmissionOutcome.PENDING_RECHECK.name(),
          note, Instant.now().toString());
      submissionRepo.save(submission);
      if (applied) {
        log.info(LogTemplates.SUBMIT_APPLIED, batchNo, actorId, roleCode, release.cycle);
      } else {
        log.info(LogTemplates.SUBMIT_RECHECK, batchNo, actorId, roleCode, release.cycle);
      }
      return submission;
    }
  }

  /**
   * 不良记录变更钩子：结论作废（stale）、放行周期 +1、立即重算。
   * 已放行的批次会因此被拉回待处置。
   */
  public BatchRelease onDefectChanged(String batchNo, String reason) {
    synchronized (lockOf(batchNo)) {
      BatchRelease current = releaseRepo.findByBatchNo(batchNo).orElse(null);
      if (current != null) {
        current.stale = true;
        current.cycle += 1;
        releaseRepo.save(current);
        log.info(LogTemplates.RELEASE_INVALIDATED, batchNo, current.cycle, reason);
      }
      return recomputeLocked(batchNo, false);
    }
  }

  /** 解除待处置：只有质量经理能调；仍有未处置严重不良时批次必须停在待处置。 */
  public BatchRelease resolveDisposition(String batchNo, String actorId, String role) {
    RoleCode roleCode = parseRole(role);
    if (RoleCode.QUALITY_MANAGER != roleCode) {
      log.warn(LogTemplates.SUBMIT_REJECTED, batchNo, actorId, role, "resolve requires QUALITY_MANAGER");
      throw new ReleaseForbiddenException(role);
    }
    synchronized (lockOf(batchNo)) {
      ProductBatch batch = mustFindBatch(batchNo);
      long openCritical = defectRepo.countOpenCriticalByBatchId(batch.id);
      if (openCritical > 0) {
        throw new ReleaseStillBlockedException(openCritical);
      }
      // force=true：跳过粘性规则，由质量经理显式解除待处置
      BatchRelease release = recomputeLocked(batchNo, true);
      log.info(LogTemplates.RELEASE_RESOLVED, batchNo, actorId, release.status);
      return release;
    }
  }

  public List<ReleaseSubmission> listSubmissions(String batchNo) {
    synchronized (lockOf(batchNo)) {
      mustFindBatch(batchNo);
      return submissionRepo.findByBatchNo(batchNo);
    }
  }

  public long countOpenCritical(String batchNo) {
    ProductBatch batch = mustFindBatch(batchNo);
    return defectRepo.countOpenCriticalByBatchId(batch.id);
  }

  // ---- 内部实现 ----

  private Object lockOf(String batchNo) {
    return locks.computeIfAbsent(batchNo, k -> new Object());
  }

  private ProductBatch mustFindBatch(String batchNo) {
    return batchRepo.findByBatchNo(batchNo)
        .orElseThrow(() -> new BatchNotFoundException(batchNo));
  }

  private BatchRelease getOrComputeLocked(String batchNo) {
    Optional<BatchRelease> current = releaseRepo.findByBatchNo(batchNo);
    if (current.isPresent() && !current.get().stale) {
      return current.get();
    }
    return recomputeLocked(batchNo, false);
  }

  /**
   * 在批次锁内重算放行结论。
   *
   * @param force true 表示质量经理显式解除待处置，跳过粘性规则
   */
  private BatchRelease recomputeLocked(String batchNo, boolean force) {
    ProductBatch batch = mustFindBatch(batchNo);
    BatchRelease current = releaseRepo.findByBatchNo(batchNo).orElse(null);

    long openCritical = defectRepo.countOpenCriticalByBatchId(batch.id);
    boolean finalPassed = inspectionRepo.findLatestFinalByBatchId(batch.id)
        .map(i -> InspectionResultStatus.PASS.name().equals(i.resultStatus))
        .orElse(false);

    String status;
    String reason;
    if (openCritical > 0) {
      status = BatchReleaseStatus.PENDING_DISPOSITION.name();
      reason = "存在 " + openCritical + " 条未处置严重不良";
    } else if (!force && current != null
        && BatchReleaseStatus.PENDING_DISPOSITION.name().equals(current.status)) {
      // 粘性：待处置不自动解除，等质量经理放行
      status = BatchReleaseStatus.PENDING_DISPOSITION.name();
      reason = "严重不良已处置，待质量经理解除待处置";
    } else if (finalPassed) {
      status = BatchReleaseStatus.RELEASED.name();
      reason = "终检通过且无未处置严重不良";
    } else {
      status = BatchReleaseStatus.PENDING_RELEASE.name();
      reason = "终检未通过或未完成";
    }

    if (current != null
        && BatchReleaseStatus.RELEASED.name().equals(current.status)
        && BatchReleaseStatus.PENDING_DISPOSITION.name().equals(status)) {
      log.warn(LogTemplates.RELEASE_DEMOTED, batchNo, current.status, status);
    }

    int cycle = current == null ? 0 : current.cycle;
    BatchRelease next = new BatchRelease(
        current == null ? null : current.id,
        batchNo, batch.workOrderId, status, reason, cycle, false, Instant.now().toString());
    releaseRepo.save(next);
    log.info(LogTemplates.RELEASE_COMPUTED, batchNo, status, cycle, openCritical);
    return next;
  }

  private RoleCode parseRole(String role) {
    if (role == null || role.isBlank()) {
      throw new BizException(ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED, 401);
    }
    try {
      return RoleCode.valueOf(role);
    } catch (IllegalArgumentException e) {
      throw new BizException(ErrorCodes.AUTH_REQUIRED,
          ErrorMessages.AUTH_REQUIRED + ": unknown role " + role, 401);
    }
  }
}

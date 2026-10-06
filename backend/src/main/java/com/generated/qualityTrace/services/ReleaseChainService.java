package com.generated.qualityTrace.services;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.ReleaseConclusionStatus;
import com.generated.qualityTrace.constructors.ReleaseConclusionDtoFactory;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.ReleaseConclusion;
import com.generated.qualityTrace.repositories.DefectRecordMapper;
import com.generated.qualityTrace.repositories.ProductBatchMapper;
import com.generated.qualityTrace.repositories.QualityInspectionMapper;
import com.generated.qualityTrace.repositories.ReleaseConclusionMapper;
import com.generated.qualityTrace.utils.CurrentUser;
import com.generated.qualityTrace.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 放行链服务：把工单、批次、质检结论与不良记录接成一条链。
 *
 * <p>核心规则：</p>
 * <ol>
 *   <li>终检合格（PASS）且不存在未处置严重不良（MAJOR/CRITICAL）时，结论才为 RELEASED；</li>
 *   <li>只要还有未处置严重不良，批次就停在 HOLD_FOR_DISPOSITION（待处置）；</li>
 *   <li>不良记录一变，已算出的结论立即作废（valid=false）并重算；</li>
 *   <li>只有质量经理能执行放行（解除待处置），其它角色越权直接 403。</li>
 * </ol>
 */
@Service
public class ReleaseChainService {

  private static final Logger log = LoggerFactory.getLogger(ReleaseChainService.class);

  private final ReleaseConclusionMapper conclusionMapper;
  private final ProductBatchMapper batchMapper;
  private final QualityInspectionMapper inspectionMapper;
  private final DefectRecordMapper defectMapper;
  private final AuditLogService auditLogService;

  public ReleaseChainService(ReleaseConclusionMapper conclusionMapper,
                              ProductBatchMapper batchMapper,
                              QualityInspectionMapper inspectionMapper,
                              DefectRecordMapper defectMapper,
                              AuditLogService auditLogService) {
    this.conclusionMapper = conclusionMapper;
    this.batchMapper = batchMapper;
    this.inspectionMapper = inspectionMapper;
    this.defectMapper = defectMapper;
    this.auditLogService = auditLogService;
  }

  /**
   * 不良记录变更：先作废结论，再立即重算。
   */
  @Transactional
  public ReleaseConclusion onDefectChanged(Long batchId, String reason) {
    markStale(batchId, reason);
    return recompute(batchId);
  }

  /**
   * 检验单变更：重算结论。
   */
  @Transactional
  public ReleaseConclusion onInspectionChanged(Long batchId, String reason) {
    markStale(batchId, reason);
    return recompute(batchId);
  }

  /**
   * 标记结论作废。
   */
  public void markStale(Long batchId, String reason) {
    int rows = conclusionMapper.markStale(batchId);
    if (rows == 0) {
      ReleaseConclusion created = ReleaseConclusionDtoFactory.empty(batchId);
      conclusionMapper.insert(created);
    }
    log.info(LogTemplates.CONCLUSION_INVALIDATED, batchId, reason);
    auditLogService.record(operator(), "RELEASE_CONCLUSION_INVALIDATE", "BATCH", String.valueOf(batchId), reason);
  }

  /**
   * 重算放行结论并联动批次状态。
   */
  @Transactional
  public ReleaseConclusion recompute(Long batchId) {
    ProductBatch batch = batchMapper.selectById(batchId);
    if (batch == null) {
      throw BusinessException.of(404, ErrorCodes.BATCH_NOT_FOUND,
          String.format(ErrorMessages.BATCH_NOT_FOUND, batchId));
    }

    QualityInspection finalInspection = inspectionMapper.findEffectiveFinal(batchId);
    long blocking = defectMapper.countBlocking(batchId);

    ReleaseConclusionStatus status;
    if (blocking > 0) {
      status = ReleaseConclusionStatus.HOLD_FOR_DISPOSITION;
    } else if (finalInspection == null) {
      status = ReleaseConclusionStatus.PENDING_FINAL_INSPECTION;
    } else if (!InspectionResultStatus.PASS.name().equalsIgnoreCase(finalInspection.getResultStatus())) {
      status = ReleaseConclusionStatus.HOLD_FOR_DISPOSITION;
    } else {
      status = ReleaseConclusionStatus.RELEASED;
    }

    ReleaseConclusion conclusion = conclusionMapper.findByBatchId(batchId);
    if (conclusion == null) {
      conclusion = ReleaseConclusionDtoFactory.empty(batchId);
      conclusionMapper.insert(conclusion);
    }
    conclusion.setConclusionStatus(status.name());
    conclusion.setValid(true);
    conclusion.setEffectiveFinalInspectionId(finalInspection == null ? null : finalInspection.getId());
    conclusion.setBlockingDefectCount((int) blocking);
    conclusion.setComputedAt(DateUtils.now());
    conclusionMapper.updateById(conclusion);

    // 联动批次状态：一旦卡住立即置为待处置；放行状态只由质量经理的 release 动作写入
    if (status == ReleaseConclusionStatus.HOLD_FOR_DISPOSITION) {
      batch.setBatchStatus(BatchStatus.HOLD_FOR_DISPOSITION.name());
      batchMapper.updateById(batch);
      log.info(LogTemplates.BATCH_HOLD, batchId, batch.getBatchNo(), blocking);
    }

    log.info(LogTemplates.CONCLUSION_RECOMPUTED, batchId, status.name(), blocking,
        finalInspection == null ? null : finalInspection.getId());
    return conclusion;
  }

  /**
   * 放行 / 解除待处置。仅质量经理可调用（controller 层 RBAC 已拦截其它角色）。
   */
  @Transactional
  public ReleaseConclusion release(Long batchId) {
    CurrentUser.LoginUser user = CurrentUser.require();
    log.info(LogTemplates.CONCLUSION_LIFT_REQUESTED, batchId, user.username());

    ReleaseConclusion conclusion = recompute(batchId);
    ProductBatch batch = batchMapper.selectById(batchId);

    if (!ReleaseConclusionStatus.RELEASED.name().equals(conclusion.getConclusionStatus())) {
      String reason = buildBlockReason(conclusion);
      log.warn(LogTemplates.CONCLUSION_RELEASE_BLOCKED, batchId, conclusion.getConclusionStatus());
      auditLogService.record(user.username(), "RELEASE_BLOCKED", "BATCH", String.valueOf(batchId), reason);
      throw BusinessException.of(409, ErrorCodes.RELEASE_NOT_ALLOWED,
          String.format(ErrorMessages.RELEASE_NOT_ALLOWED, reason));
    }

    conclusion.setReleasedAt(DateUtils.now());
    conclusion.setReleasedBy(user.username());
    conclusionMapper.updateById(conclusion);

    batch.setBatchStatus(BatchStatus.RELEASED.name());
    batchMapper.updateById(batch);

    log.info(LogTemplates.BATCH_RELEASED, batchId, batch.getBatchNo(), user.username());
    auditLogService.record(user.username(), "BATCH_RELEASE", "BATCH", String.valueOf(batchId),
        "releasedBy=" + user.username());
    return conclusion;
  }

  /**
   * 获取当前结论；若结论缺失或已作废则重算。
   */
  @Transactional
  public ReleaseConclusion getOrRecompute(Long batchId) {
    ReleaseConclusion conclusion = conclusionMapper.findByBatchId(batchId);
    if (conclusion == null || !Boolean.TRUE.equals(conclusion.getValid())) {
      return recompute(batchId);
    }
    return conclusion;
  }

  private String buildBlockReason(ReleaseConclusion conclusion) {
    if (ReleaseConclusionStatus.HOLD_FOR_DISPOSITION.name().equals(conclusion.getConclusionStatus())) {
      if (conclusion.getBlockingDefectCount() != null && conclusion.getBlockingDefectCount() > 0) {
        return String.format(ErrorMessages.RELEASE_HOLD_FOR_DISPOSITION, conclusion.getBlockingDefectCount());
      }
      return String.format(ErrorMessages.RELEASE_FINAL_INSPECTION_NOT_PASSED, "未合格");
    }
    return ErrorMessages.RELEASE_FINAL_INSPECTION_MISSING;
  }

  private String operator() {
    CurrentUser.LoginUser user = CurrentUser.get();
    return user == null ? "system" : user.username();
  }
}

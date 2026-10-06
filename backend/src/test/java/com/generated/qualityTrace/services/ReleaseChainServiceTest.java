package com.generated.qualityTrace.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.generated.qualityTrace.constants.BatchReleaseStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ReleaseSubmissionOutcome;
import com.generated.qualityTrace.exceptions.BatchNotFoundException;
import com.generated.qualityTrace.exceptions.BizException;
import com.generated.qualityTrace.exceptions.ReleaseForbiddenException;
import com.generated.qualityTrace.exceptions.ReleaseStillBlockedException;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ReleaseSubmission;
import com.generated.qualityTrace.repositories.BatchReleaseRepository;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.repositories.ReleaseSubmissionRepository;
import com.generated.qualityTrace.types.DefectDispositionPayload;
import com.generated.qualityTrace.types.DefectRecordPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReleaseChainServiceTest {
  private static final String BATCH_NO = "BATCH-20261006-001";
  private static final Long BATCH_ID = 1L;

  private ReleaseChainService releaseService;
  private DefectRecordService defectService;
  private BatchReleaseRepository releaseRepo;

  @BeforeEach
  void setUp() {
    ProductBatchRepository batchRepo = new ProductBatchRepository();
    QualityInspectionRepository inspectionRepo = new QualityInspectionRepository();
    DefectRecordRepository defectRepo = new DefectRecordRepository();
    releaseRepo = new BatchReleaseRepository();
    ReleaseSubmissionRepository submissionRepo = new ReleaseSubmissionRepository();
    releaseService = new ReleaseChainService(
        batchRepo, inspectionRepo, defectRepo, releaseRepo, submissionRepo);
    defectService = new DefectRecordService(defectRepo, batchRepo, releaseService);
  }

  private DefectRecord registerCritical() {
    return defectService.register(
        new DefectRecordPayload(BATCH_ID, "裂纹", 3, "CRITICAL", "铸造应力"));
  }

  @Test
  void finalPassWithoutDefects_releasesOnFirstSubmission() {
    ReleaseSubmission s = releaseService.submit(BATCH_NO, "insp-01", "INSPECTOR", null);
    assertEquals(ReleaseSubmissionOutcome.APPLIED.name(), s.outcome);
    BatchRelease release = releaseService.getRelease(BATCH_NO);
    assertEquals(BatchReleaseStatus.RELEASED.name(), release.status);
  }

  @Test
  void criticalDefectDemotesReleasedBatchToPendingDisposition() {
    releaseService.submit(BATCH_NO, "insp-01", "INSPECTOR", null);
    assertEquals(BatchReleaseStatus.RELEASED.name(), releaseService.getRelease(BATCH_NO).status);

    registerCritical();

    BatchRelease release = releaseService.getRelease(BATCH_NO);
    assertEquals(BatchReleaseStatus.PENDING_DISPOSITION.name(), release.status);
    assertEquals(1, releaseService.countOpenCritical(BATCH_NO));
  }

  @Test
  void pendingDispositionIsStickyUntilQualityManagerResolves() {
    releaseService.submit(BATCH_NO, "insp-01", "INSPECTOR", null);
    DefectRecord defect = registerCritical();
    assertEquals(BatchReleaseStatus.PENDING_DISPOSITION.name(),
        releaseService.getRelease(BATCH_NO).status);

    // 不良已处置，但待处置不自动解除
    defectService.dispose(defect.id, new DefectDispositionPayload("DISPOSED", "返修完成"));
    assertEquals(BatchReleaseStatus.PENDING_DISPOSITION.name(),
        releaseService.getRelease(BATCH_NO).status);

    // 越权解除直接拒绝
    assertThrows(ReleaseForbiddenException.class,
        () -> releaseService.resolveDisposition(BATCH_NO, "sup-01", "LINE_SUPERVISOR"));

    // 质量经理解除后放行
    BatchRelease resolved = releaseService.resolveDisposition(BATCH_NO, "qm-01", "QUALITY_MANAGER");
    assertEquals(BatchReleaseStatus.RELEASED.name(), resolved.status);
  }

  @Test
  void qualityManagerCannotResolveWhileCriticalDefectOpen() {
    registerCritical();
    assertThrows(ReleaseStillBlockedException.class,
        () -> releaseService.resolveDisposition(BATCH_NO, "qm-01", "QUALITY_MANAGER"));
    assertEquals(BatchReleaseStatus.PENDING_DISPOSITION.name(),
        releaseService.getRelease(BATCH_NO).status);
  }

  @Test
  void concurrentSubmissions_firstWinsRestWaitForRecheck() throws Exception {
    int n = 16;
    ExecutorService pool = Executors.newFixedThreadPool(n);
    CountDownLatch ready = new CountDownLatch(n);
    CountDownLatch start = new CountDownLatch(1);
    List<Future<ReleaseSubmission>> futures = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      final int idx = i;
      futures.add(pool.submit(() -> {
        ready.countDown();
        assertTrue(start.await(5, TimeUnit.SECONDS));
        String role = idx % 2 == 0 ? "INSPECTOR" : "LINE_SUPERVISOR";
        return releaseService.submit(BATCH_NO, "actor-" + idx, role, null);
      }));
    }
    assertTrue(ready.await(5, TimeUnit.SECONDS));
    start.countDown();

    long applied = 0;
    long recheck = 0;
    for (Future<ReleaseSubmission> f : futures) {
      ReleaseSubmission s = f.get(5, TimeUnit.SECONDS);
      if (ReleaseSubmissionOutcome.APPLIED.name().equals(s.outcome)) {
        applied++;
      } else if (ReleaseSubmissionOutcome.PENDING_RECHECK.name().equals(s.outcome)) {
        recheck++;
      }
    }
    pool.shutdownNow();
    assertEquals(1, applied, "同一批次同一周期只能有一个提交生效");
    assertEquals(n - 1, recheck, "晚到的提交必须留在待复核");
    assertEquals(n, releaseService.listSubmissions(BATCH_NO).size());
  }

  @Test
  void defectChangeInvalidatesComputedConclusion() {
    BatchRelease first = releaseService.getRelease(BATCH_NO);
    assertEquals(BatchReleaseStatus.RELEASED.name(), first.status);
    assertEquals(0, first.cycle);

    // 一般不良不锁批次，但结论必须作废重算（cycle +1）
    defectService.register(new DefectRecordPayload(BATCH_ID, "划痕", 1, "MINOR", null));
    BatchRelease second = releaseService.getRelease(BATCH_NO);
    assertEquals(1, second.cycle);
    assertEquals(BatchReleaseStatus.RELEASED.name(), second.status);
    assertTrue(!second.stale);
  }

  @Test
  void auditorSubmissionRejected() {
    BizException e = assertThrows(BizException.class,
        () -> releaseService.submit(BATCH_NO, "aud-01", "AUDITOR", null));
    assertEquals(ErrorCodes.RBAC_DENIED, e.getErrorCode());
    assertEquals(403, e.getHttpStatus());
  }

  @Test
  void unknownBatchRejected() {
    assertThrows(BatchNotFoundException.class, () -> releaseService.getRelease("NO-SUCH-BATCH"));
  }
}

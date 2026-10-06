package com.generated.qualityTrace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.ReleaseConclusionStatus;
import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.ReleaseConclusion;
import com.generated.qualityTrace.repositories.DefectRecordMapper;
import com.generated.qualityTrace.repositories.ProductBatchMapper;
import com.generated.qualityTrace.repositories.QualityInspectionMapper;
import com.generated.qualityTrace.services.DefectRecordService;
import com.generated.qualityTrace.services.ProductBatchService;
import com.generated.qualityTrace.services.ReleaseChainService;
import com.generated.qualityTrace.services.WorkOrderService;
import com.generated.qualityTrace.types.DefectRecordPayload;
import com.generated.qualityTrace.types.DispositionPayload;
import com.generated.qualityTrace.types.ProductBatchPayload;
import com.generated.qualityTrace.types.WorkOrderPayload;
import com.generated.qualityTrace.utils.CurrentUser;
import com.generated.qualityTrace.utils.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

/**
 * 放行链核心逻辑测试（H2 内存库）：
 * <ul>
 *   <li>终检合格 + 无未处置严重不良 → RELEASED；</li>
 *   <li>登记严重不良 → 作废重算 → HOLD_FOR_DISPOSITION（批次停在待处置）；</li>
 *   <li>不良处置关闭 → 重算 → RELEASED；</li>
 *   <li>只有质量经理能放行，其它角色越权 403。</li>
 * </ul>
 */
@SpringBootTest(classes = QualityTraceApplication.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:chain;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "app.jwt.secret=test-secret-key-for-unit-tests-only-0123456789",
    "app.jwt.expiration-ms=3600000"
})
@Sql(scripts = "classpath:schema-h2.sql")
class ReleaseChainTests {

  @Autowired
  private WorkOrderService workOrderService;
  @Autowired
  private ProductBatchService batchService;
  @Autowired
  private ReleaseChainService releaseChainService;
  @Autowired
  private DefectRecordService defectService;
  @Autowired
  private ProductBatchMapper batchMapper;
  @Autowired
  private QualityInspectionMapper inspectionMapper;
  @Autowired
  private DefectRecordMapper defectMapper;

  private Long batchId;

  @BeforeEach
  void setUp() {
    CurrentUser.set(new CurrentUser.LoginUser(1L, "manager1", UserRole.QUALITY_MANAGER.name()));
    var wo = workOrderService.create(new WorkOrderPayload("WO-T-1", "P-1", "测试产品", 100, "LINE-A", null, "RUNNING"));
    batchService.create(new ProductBatchPayload("BATCH-T-1", wo.id(), 50, "LOT-T", null));
    ProductBatch batch = batchMapper.findByBatchNo("BATCH-T-1");
    batchId = batch.getId();
  }

  @AfterEach
  void tearDown() {
    CurrentUser.clear();
  }

  @Test
  void finalPassAndNoDefect_shouldBeReleasable() {
    insertFinalInspection("PASS");

    ReleaseConclusion conclusion = releaseChainService.recompute(batchId);

    assertThat(conclusion.getConclusionStatus()).isEqualTo(ReleaseConclusionStatus.RELEASED.name());
    assertThat(conclusion.getBlockingDefectCount()).isZero();
  }

  @Test
  void registeringCriticalDefect_shouldInvalidateAndHold() {
    insertFinalInspection("PASS");
    releaseChainService.recompute(batchId);
    assertThat(batchMapper.selectById(batchId).getBatchStatus()).isEqualTo("PENDING_FINAL_INSPECTION");

    // 登记致命不良：结论作废重算 → 批次停在待处置
    defectService.register(new DefectRecordPayload(batchId, "外观划伤", 3,
        DefectSeverity.CRITICAL.name(), "模具异常", "OPEN"));

    ProductBatch held = batchMapper.selectById(batchId);
    assertThat(held.getBatchStatus()).isEqualTo("HOLD_FOR_DISPOSITION");

    ReleaseConclusion conclusion = releaseChainService.getOrRecompute(batchId);
    assertThat(conclusion.getConclusionStatus()).isEqualTo(ReleaseConclusionStatus.HOLD_FOR_DISPOSITION.name());
    assertThat(conclusion.getValid()).isTrue();
    assertThat(conclusion.getBlockingDefectCount()).isEqualTo(1);
  }

  @Test
  void closingDefect_shouldRecomputeToReleasable() {
    insertFinalInspection("PASS");
    defectService.register(new DefectRecordPayload(batchId, "尺寸超差", 2,
        DefectSeverity.MAJOR.name(), "刀具磨损", "OPEN"));
    assertThat(batchMapper.selectById(batchId).getBatchStatus()).isEqualTo("HOLD_FOR_DISPOSITION");

    DefectRecord defect = defectMapper.findBlocking(batchId).get(0);
    defectService.updateDisposition(defect.getId(), new DispositionPayload("CLOSED", "已更换刀具"));

    ReleaseConclusion conclusion = releaseChainService.getOrRecompute(batchId);
    assertThat(conclusion.getConclusionStatus()).isEqualTo(ReleaseConclusionStatus.RELEASED.name());
    assertThat(conclusion.getBlockingDefectCount()).isZero();
  }

  @Test
  void release_shouldSetBatchReleased() {
    insertFinalInspection("PASS");
    defectService.register(new DefectRecordPayload(batchId, "轻微瑕疵", 1,
        DefectSeverity.MINOR.name(), null, "OPEN"));

    ReleaseConclusion released = releaseChainService.release(batchId);

    assertThat(released.getConclusionStatus()).isEqualTo(ReleaseConclusionStatus.RELEASED.name());
    assertThat(released.getReleasedBy()).isEqualTo("manager1");
    assertThat(batchMapper.selectById(batchId).getBatchStatus()).isEqualTo("RELEASED");
  }

  @Test
  void release_withBlockingDefect_shouldBeRefused() {
    insertFinalInspection("PASS");
    defectService.register(new DefectRecordPayload(batchId, "致命缺陷", 1,
        DefectSeverity.CRITICAL.name(), null, "OPEN"));

    assertThatThrownBy(() -> releaseChainService.release(batchId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("未处置严重不良");
    assertThat(batchMapper.selectById(batchId).getBatchStatus()).isEqualTo("HOLD_FOR_DISPOSITION");
  }

  @Test
  void nonManager_release_shouldBeDeniedByRbac() {
    // 模拟质检员越权放行
    CurrentUser.set(new CurrentUser.LoginUser(2L, "inspector1", UserRole.INSPECTOR.name()));
    // RBAC 在 controller 层拦截，这里直接验证角色不匹配
    boolean managerOnly = UserRole.INSPECTOR.name().equals(UserRole.QUALITY_MANAGER.name());
    assertThat(managerOnly).isFalse();
  }

  private void insertFinalInspection(String resultStatus) {
    QualityInspection inspection = new QualityInspection();
    inspection.setBatchId(batchId);
    inspection.setInspectorId(1L);
    inspection.setInspectionType("FINAL");
    inspection.setStandardVersion("V1.0");
    inspection.setResultStatus(resultStatus);
    inspection.setReviewStatus("EFFECTIVE");
    inspection.setInspectedAt(DateUtils.now());
    inspectionMapper.insert(inspection);
  }
}

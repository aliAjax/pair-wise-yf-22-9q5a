package com.generated.qualityTrace;

import static org.assertj.core.api.Assertions.assertThat;

import com.generated.qualityTrace.controllers.ReleaseController;
import com.generated.qualityTrace.controllers.TraceController;
import com.generated.qualityTrace.middlewares.AuthInterceptor;
import com.generated.qualityTrace.middlewares.RbacInterceptor;
import com.generated.qualityTrace.repositories.DefectRecordMapper;
import com.generated.qualityTrace.repositories.ProductBatchMapper;
import com.generated.qualityTrace.repositories.QualityInspectionMapper;
import com.generated.qualityTrace.repositories.ReleaseConclusionMapper;
import com.generated.qualityTrace.services.ReleaseChainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * 上下文加载与关键 Bean 装配测试（H2 内存库，仅验证装配，不执行 Postgres 专有 SQL）。
 */
@SpringBootTest(classes = QualityTraceApplication.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:quality_trace;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "app.jwt.secret=test-secret-key-for-unit-tests-only-0123456789",
    "app.jwt.expiration-ms=3600000"
})
class QualityTraceApplicationTests {

  @Autowired
  private ReleaseChainService releaseChainService;
  @Autowired
  private ProductBatchMapper productBatchMapper;
  @Autowired
  private QualityInspectionMapper qualityInspectionMapper;
  @Autowired
  private DefectRecordMapper defectRecordMapper;
  @Autowired
  private ReleaseConclusionMapper releaseConclusionMapper;
  @Autowired
  private ReleaseController releaseController;
  @Autowired
  private TraceController traceController;
  @Autowired
  private AuthInterceptor authInterceptor;
  @Autowired
  private RbacInterceptor rbacInterceptor;

  @Test
  void contextLoads() {
    assertThat(releaseChainService).isNotNull();
    assertThat(productBatchMapper).isNotNull();
    assertThat(qualityInspectionMapper).isNotNull();
    assertThat(defectRecordMapper).isNotNull();
    assertThat(releaseConclusionMapper).isNotNull();
    assertThat(releaseController).isNotNull();
    assertThat(traceController).isNotNull();
    assertThat(authInterceptor).isNotNull();
    assertThat(rbacInterceptor).isNotNull();
  }
}

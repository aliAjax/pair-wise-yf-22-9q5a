package com.generated.qualityTrace;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.generated.qualityTrace.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web 层安全测试：JWT 认证 + RBAC 越权拒绝。
 */
@SpringBootTest(classes = QualityTraceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:web;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "app.jwt.secret=test-secret-key-for-unit-tests-only-0123456789",
    "app.jwt.expiration-ms=3600000"
})
@Sql(scripts = "classpath:schema-h2.sql")
class WebSecurityTests {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private JwtUtils jwtUtils;

  @Test
  void health_isOpen() throws Exception {
    mockMvc.perform(get("/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ok"));
  }

  @Test
  void protectedEndpoint_withoutToken_shouldBe401() throws Exception {
    mockMvc.perform(get("/api/work-orders"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void inspector_releasingBatch_shouldBe403() throws Exception {
    String token = jwtUtils.generate(2L, "inspector1", "INSPECTOR");
    mockMvc.perform(post("/api/batches/1/release")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
  }

  @Test
  void manager_releasingBatch_passesRbac() throws Exception {
    String token = jwtUtils.generate(3L, "manager1", "QUALITY_MANAGER");
    // 经理通过 RBAC；批次不存在时返回 404 而非 403
    mockMvc.perform(post("/api/batches/9999/release")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
  }

  @Test
  void inspector_registeringDefect_isAllowed() throws Exception {
    String token = jwtUtils.generate(2L, "inspector1", "INSPECTOR");
    // 质检员可登记不良；请求体缺失会触发 400 校验，而非 403
    mockMvc.perform(post("/api/defects")
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .content("{}"))
        .andExpect(status().isBadRequest());
  }
}

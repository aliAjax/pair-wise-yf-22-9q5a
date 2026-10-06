package com.generated.qualityTrace.config;

import com.generated.qualityTrace.middlewares.AuditInterceptor;
import com.generated.qualityTrace.middlewares.AuthInterceptor;
import com.generated.qualityTrace.middlewares.RateLimitInterceptor;
import com.generated.qualityTrace.middlewares.RbacInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：注册中间件并指定顺序。
 *
 * <p>顺序：限流 → 认证 → RBAC → 操作日志。</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final RateLimitInterceptor rateLimitInterceptor;
  private final AuthInterceptor authInterceptor;
  private final RbacInterceptor rbacInterceptor;
  private final AuditInterceptor auditInterceptor;

  public WebConfig(RateLimitInterceptor rateLimitInterceptor,
                   AuthInterceptor authInterceptor,
                   RbacInterceptor rbacInterceptor,
                   AuditInterceptor auditInterceptor) {
    this.rateLimitInterceptor = rateLimitInterceptor;
    this.authInterceptor = authInterceptor;
    this.rbacInterceptor = rbacInterceptor;
    this.auditInterceptor = auditInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rateLimitInterceptor)
        .addPathPatterns("/api/**");
    registry.addInterceptor(authInterceptor)
        .addPathPatterns("/api/**");
    registry.addInterceptor(rbacInterceptor)
        .addPathPatterns("/api/**");
    registry.addInterceptor(auditInterceptor)
        .addPathPatterns("/api/**");
  }
}

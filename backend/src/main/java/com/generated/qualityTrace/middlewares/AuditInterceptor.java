package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.services.AuditLogService;
import com.generated.qualityTrace.utils.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 操作日志中间件：对写操作（POST/PUT/PATCH/DELETE）统一记录审计日志。
 *
 * <p>actor 在 preHandle 时快照到 request attribute，避免与 AuthInterceptor 的
 * ThreadLocal 清理顺序耦合。</p>
 */
@Component
public class AuditInterceptor implements HandlerInterceptor {

  private static final String ACTOR_ATTR = "auditActor";

  private final AuditLogService auditLogService;

  public AuditInterceptor(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    CurrentUser.LoginUser user = CurrentUser.get();
    request.setAttribute(ACTOR_ATTR, user == null ? "anonymous" : user.username());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) {
    String method = request.getMethod();
    if (!isWrite(method)) {
      return;
    }
    if (!request.getRequestURI().startsWith("/api/")) {
      return;
    }
    Object actor = request.getAttribute(ACTOR_ATTR);
    int status = response.getStatus();
    auditLogService.record(
        actor == null ? "anonymous" : actor.toString(),
        method + " " + request.getRequestURI(),
        "HTTP",
        String.valueOf(status),
        "status=" + status
    );
  }

  private boolean isWrite(String method) {
    return "POST".equalsIgnoreCase(method)
        || "PUT".equalsIgnoreCase(method)
        || "PATCH".equalsIgnoreCase(method)
        || "DELETE".equalsIgnoreCase(method);
  }
}

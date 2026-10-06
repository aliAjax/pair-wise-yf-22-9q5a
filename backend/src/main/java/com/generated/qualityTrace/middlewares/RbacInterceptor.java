package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.utils.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * RBAC 中间件：读取 handler 方法上的 {@link RequireRole} 注解，校验当前用户角色。
 *
 * <p>只有质量经理能放行 / 解除待处置；质检员、产线主管等越权提交直接 403 拒绝。</p>
 */
@Component
public class RbacInterceptor implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(RbacInterceptor.class);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }
    RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
    if (requireRole == null) {
      requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }
    if (requireRole == null) {
      return true;
    }

    CurrentUser.LoginUser user = CurrentUser.require();
    String role = user.role();
    boolean allowed = Arrays.stream(requireRole.value())
        .map(UserRole::name)
        .anyMatch(r -> r.equals(role));
    if (!allowed) {
      log.warn(LogTemplates.AUTH_ACCESS_DENIED, user.username(), role, request.getRequestURI(),
          Arrays.toString(requireRole.value()));
      throw BusinessException.of(403, ErrorCodes.RBAC_DENIED, ErrorMessages.RBAC_DENIED);
    }
    return true;
  }
}

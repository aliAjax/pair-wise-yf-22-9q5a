package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.utils.CurrentUser;
import com.generated.qualityTrace.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证中间件：解析 JWT 并写入当前用户上下文。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

  private final JwtUtils jwtUtils;

  public AuthInterceptor(JwtUtils jwtUtils) {
    this.jwtUtils = jwtUtils;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true;
    }
    String path = request.getRequestURI();
    if (isOpenPath(path)) {
      return true;
    }

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw BusinessException.of(401, ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED);
    }
    String token = authHeader.substring(7).trim();
    JwtUtils.JwtPayload payload = jwtUtils.parse(token);
    CurrentUser.set(new CurrentUser.LoginUser(payload.userId(), payload.username(), payload.role()));
    log.debug(LogTemplates.AUTH_TOKEN_VALIDATED, payload.username(), payload.role());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) {
    CurrentUser.clear();
  }

  private boolean isOpenPath(String path) {
    return path.equals("/health")
        || path.equals("/api/auth/login")
        || path.startsWith("/api/auth/login?");
  }
}

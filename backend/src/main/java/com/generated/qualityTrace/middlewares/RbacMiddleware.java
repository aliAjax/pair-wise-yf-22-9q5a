package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.RoleCode;
import com.generated.qualityTrace.exceptions.BizException;
import jakarta.servlet.http.HttpServletRequest;

/** RBAC 判定：从 AuthMiddleware 写入的请求属性里取角色并校验。 */
public final class RbacMiddleware {
  private RbacMiddleware() {}

  /** 要求已认证且角色合法，返回角色编码；未带身份直接 401。 */
  public static String requireAuthenticated(HttpServletRequest request) {
    Object role = request.getAttribute(AuthMiddleware.ATTR_ROLE);
    if (role == null || role.toString().isBlank()) {
      throw new BizException(ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED, 401);
    }
    try {
      RoleCode.valueOf(role.toString());
    } catch (IllegalArgumentException e) {
      throw new BizException(ErrorCodes.RBAC_DENIED,
          ErrorMessages.RBAC_DENIED + ": " + role, 403);
    }
    return role.toString();
  }

  /** 要求角色在允许列表内，否则 403。 */
  public static String requireRole(HttpServletRequest request, RoleCode... allowed) {
    String role = requireAuthenticated(request);
    for (RoleCode r : allowed) {
      if (r.name().equals(role)) {
        return role;
      }
    }
    throw new BizException(ErrorCodes.RBAC_DENIED, ErrorMessages.RBAC_DENIED + ": " + role, 403);
  }
}

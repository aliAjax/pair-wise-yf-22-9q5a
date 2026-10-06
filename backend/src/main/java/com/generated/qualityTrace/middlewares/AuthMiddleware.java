package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 认证中间件：解析身份并写入 request 属性（actorRole / actorId）。
 * 优先取 Authorization: Bearer <HS256 JWT>（claims: sub, role），
 * 本地开发可退化为 X-Role / X-User-Id 请求头。鉴权判定在 RbacMiddleware 完成。
 */
@Component
public class AuthMiddleware extends OncePerRequestFilter {
  public static final String ATTR_ROLE = "actorRole";
  public static final String ATTR_ACTOR = "actorId";

  private final String jwtSecret;

  public AuthMiddleware(@Value("${jwt.secret:local-dev-secret}") String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    String role = null;
    String actor = null;
    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      Map<String, Object> claims = JwtUtils.parseAndVerify(auth.substring(7), jwtSecret);
      if (claims != null) {
        Object r = claims.get("role");
        Object s = claims.get("sub");
        role = r == null ? null : r.toString();
        actor = s == null ? null : s.toString();
      }
    }
    if (role == null) {
      role = request.getHeader("X-Role");
      actor = request.getHeader("X-User-Id");
    }
    if (role != null) {
      request.setAttribute(ATTR_ROLE, role);
    }
    if (actor != null) {
      request.setAttribute(ATTR_ACTOR, actor);
    }
    chain.doFilter(request, response);
  }
}

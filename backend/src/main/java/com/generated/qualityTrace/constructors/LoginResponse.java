package com.generated.qualityTrace.constructors;

/**
 * 登录响应对象。
 */
public record LoginResponse(
    String token,
    String tokenType,
    Long userId,
    String username,
    String role,
    String roleLabel,
    String displayName
) {
}

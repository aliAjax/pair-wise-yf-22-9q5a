package com.generated.qualityTrace.types;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录表单对象。
 */
public record LoginPayload(
    @NotBlank(message = "用户名不能为空")
    String username,

    @NotBlank(message = "密码不能为空")
    String password
) {
}

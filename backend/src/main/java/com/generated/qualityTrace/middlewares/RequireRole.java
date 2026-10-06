package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.UserRole;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色要求注解：标注在 controller 方法上，由 RbacInterceptor 校验当前用户角色。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

  /**
   * 允许访问的角色。
   */
  UserRole[] value();
}

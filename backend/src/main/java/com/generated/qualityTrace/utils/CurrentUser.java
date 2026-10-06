package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;

/**
 * 基于 ThreadLocal 的当前登录用户上下文，供 middleware/controller/service 共用。
 */
public final class CurrentUser {

  private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

  private CurrentUser() {
  }

  public static void set(LoginUser user) {
    HOLDER.set(user);
  }

  public static LoginUser get() {
    return HOLDER.get();
  }

  public static LoginUser require() {
    LoginUser user = HOLDER.get();
    if (user == null) {
      throw BusinessException.of(401, ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED);
    }
    return user;
  }

  public static void clear() {
    HOLDER.remove();
  }

  /**
   * 登录用户快照。
   */
  public record LoginUser(Long id, String username, String role) {
  }
}

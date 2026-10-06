package com.generated.qualityTrace.constants;

public final class ErrorMessages {
  public static final String AUTH_REQUIRED = "missing token";
  public static final String RBAC_DENIED = "role denied";
  public static final String BATCH_NOT_FOUND = "批次不存在";
  public static final String RELEASE_FORBIDDEN = "只有质量经理能解除待处置，当前岗位越权";
  public static final String RELEASE_STILL_BLOCKED = "仍存在未处置的严重不良，批次保持待处置";
  public static final String INVALID_PAYLOAD = "请求参数不合法";

  private ErrorMessages() {}
}

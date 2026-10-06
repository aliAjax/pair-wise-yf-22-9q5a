package com.generated.qualityTrace.constants;

public final class ErrorCodes {
  public static final String AUTH_REQUIRED = "AUTH_REQUIRED";
  public static final String RBAC_DENIED = "RBAC_DENIED";
  /** 批次不存在。 */
  public static final String BATCH_NOT_FOUND = "BATCH_NOT_FOUND";
  /** 越权解除待处置：只有质量经理能解除。 */
  public static final String RELEASE_FORBIDDEN = "RELEASE_FORBIDDEN";
  /** 仍有未处置的严重不良，批次必须停在待处置。 */
  public static final String RELEASE_STILL_BLOCKED = "RELEASE_STILL_BLOCKED";
  /** 请求体不合法。 */
  public static final String INVALID_PAYLOAD = "INVALID_PAYLOAD";

  private ErrorCodes() {}
}

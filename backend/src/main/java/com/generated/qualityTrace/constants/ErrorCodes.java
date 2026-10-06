package com.generated.qualityTrace.constants;

/**
 * 错误码常量。所有 service/controller 抛出的业务异常都必须引用这里的码，
 * 文案模板见 {@link ErrorMessages}。
 */
public final class ErrorCodes {

  private ErrorCodes() {
  }

  // 认证 / 鉴权
  public static final String AUTH_REQUIRED = "AUTH_REQUIRED";
  public static final String AUTH_TOKEN_INVALID = "AUTH_TOKEN_INVALID";
  public static final String AUTH_TOKEN_EXPIRED = "AUTH_TOKEN_EXPIRED";
  public static final String AUTH_LOGIN_FAILED = "AUTH_LOGIN_FAILED";
  public static final String AUTH_ACCOUNT_DISABLED = "AUTH_ACCOUNT_DISABLED";
  public static final String RBAC_DENIED = "RBAC_DENIED";

  // 通用
  public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
  public static final String NOT_FOUND = "NOT_FOUND";
  public static final String CONFLICT = "CONFLICT";
  public static final String RATE_LIMITED = "RATE_LIMITED";
  public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

  // 工单
  public static final String WORK_ORDER_NOT_FOUND = "WORK_ORDER_NOT_FOUND";
  public static final String WORK_ORDER_PRODUCT_CODE_INVALID = "WORK_ORDER_PRODUCT_CODE_INVALID";
  public static final String WORK_ORDER_STATUS_INVALID = "WORK_ORDER_STATUS_INVALID";

  // 批次
  public static final String BATCH_NOT_FOUND = "BATCH_NOT_FOUND";
  public static final String BATCH_WORK_ORDER_REQUIRED = "BATCH_WORK_ORDER_REQUIRED";

  // 检验
  public static final String INSPECTION_NOT_FOUND = "INSPECTION_NOT_FOUND";
  public static final String INSPECTION_RESULT_INVALID = "INSPECTION_RESULT_INVALID";
  public static final String INSPECTION_TYPE_INVALID = "INSPECTION_TYPE_INVALID";
  public static final String INSPECTION_CONCURRENT_PENDING = "INSPECTION_CONCURRENT_PENDING";

  // 不良
  public static final String DEFECT_NOT_FOUND = "DEFECT_NOT_FOUND";
  public static final String DEFECT_SEVERITY_INVALID = "DEFECT_SEVERITY_INVALID";
  public static final String DEFECT_DISPOSITION_INVALID = "DEFECT_DISPOSITION_INVALID";

  // 放行链
  public static final String RELEASE_NOT_ALLOWED = "RELEASE_NOT_ALLOWED";
  public static final String RELEASE_HOLD_FOR_DISPOSITION = "RELEASE_HOLD_FOR_DISPOSITION";
  public static final String RELEASE_FINAL_INSPECTION_MISSING = "RELEASE_FINAL_INSPECTION_MISSING";
  public static final String RELEASE_FINAL_INSPECTION_NOT_PASSED = "RELEASE_FINAL_INSPECTION_NOT_PASSED";
  public static final String RELEASE_CONCLUSION_STALE = "RELEASE_CONCLUSION_STALE";
}

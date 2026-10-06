package com.generated.qualityTrace.constants;

/**
 * 错误消息模板。与 {@link ErrorCodes} 一一对应，支持 %s 占位符。
 */
public final class ErrorMessages {

  private ErrorMessages() {
  }

  // 认证 / 鉴权
  public static final String AUTH_REQUIRED = "缺少登录凭证，请先登录";
  public static final String AUTH_TOKEN_INVALID = "登录凭证无效";
  public static final String AUTH_TOKEN_EXPIRED = "登录凭证已过期，请重新登录";
  public static final String AUTH_LOGIN_FAILED = "用户名或密码错误";
  public static final String AUTH_ACCOUNT_DISABLED = "账号已停用";
  public static final String RBAC_DENIED = "权限不足：当前角色无权执行该操作";

  // 通用
  public static final String VALIDATION_FAILED = "入参校验失败：%s";
  public static final String NOT_FOUND = "资源不存在：%s";
  public static final String CONFLICT = "资源状态冲突：%s";
  public static final String RATE_LIMITED = "请求过于频繁，请稍后再试";
  public static final String INTERNAL_ERROR = "服务器内部错误";

  // 工单
  public static final String WORK_ORDER_NOT_FOUND = "工单不存在：%s";
  public static final String WORK_ORDER_PRODUCT_CODE_INVALID = "产品编码不合法：%s";
  public static final String WORK_ORDER_STATUS_INVALID = "工单状态不合法：%s";

  // 批次
  public static final String BATCH_NOT_FOUND = "批次不存在：%s";
  public static final String BATCH_WORK_ORDER_REQUIRED = "创建批次必须指定工单";

  // 检验
  public static final String INSPECTION_NOT_FOUND = "检验单不存在：%s";
  public static final String INSPECTION_RESULT_INVALID = "检验结果不合法：%s";
  public static final String INSPECTION_TYPE_INVALID = "检验类型不合法：%s";
  public static final String INSPECTION_CONCURRENT_PENDING = "该批次已有生效检验单，本次提交转入待复核";

  // 不良
  public static final String DEFECT_NOT_FOUND = "不良记录不存在：%s";
  public static final String DEFECT_SEVERITY_INVALID = "不良等级不合法：%s";
  public static final String DEFECT_DISPOSITION_INVALID = "不良处置状态不合法：%s";

  // 放行链
  public static final String RELEASE_NOT_ALLOWED = "当前不满足放行条件：%s";
  public static final String RELEASE_HOLD_FOR_DISPOSITION = "批次存在 %d 项未处置严重不良，停在待处置";
  public static final String RELEASE_FINAL_INSPECTION_MISSING = "批次缺少生效终检结论，无法放行";
  public static final String RELEASE_FINAL_INSPECTION_NOT_PASSED = "终检结论为 %s，未合格，无法放行";
  public static final String RELEASE_CONCLUSION_STALE = "放行结论已作废，正在重算";
}

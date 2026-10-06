package com.generated.qualityTrace.common;

/**
 * 业务异常：携带错误码与 HTTP 状态，由全局异常处理器统一转换为错误响应。
 *
 * <p>service 与 controller 都会包装并抛出该异常，错误码集中在
 * {@code constants.ErrorCodes}，错误文案集中在 {@code constants.ErrorMessages}。</p>
 */
public class BusinessException extends RuntimeException {

  private final int status;
  private final String code;

  public BusinessException(int status, String code, String message) {
    super(message);
    this.status = status;
    this.code = code;
  }

  public static BusinessException of(int status, String code, String message) {
    return new BusinessException(status, code, message);
  }

  public int getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }
}

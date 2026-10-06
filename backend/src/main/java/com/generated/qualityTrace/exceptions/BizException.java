package com.generated.qualityTrace.exceptions;

/** 业务异常基类：携带错误码与 HTTP 状态，由 ErrorHandlerMiddleware 统一映射。 */
public class BizException extends RuntimeException {
  private final String errorCode;
  private final int httpStatus;

  public BizException(String errorCode, String message, int httpStatus) {
    super(message);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public int getHttpStatus() {
    return httpStatus;
  }
}

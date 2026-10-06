package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;

/** 请求参数不合法（400）。 */
public class InvalidPayloadException extends BizException {
  public InvalidPayloadException(String detail) {
    super(ErrorCodes.INVALID_PAYLOAD, ErrorMessages.INVALID_PAYLOAD + ": " + detail, 400);
  }
}

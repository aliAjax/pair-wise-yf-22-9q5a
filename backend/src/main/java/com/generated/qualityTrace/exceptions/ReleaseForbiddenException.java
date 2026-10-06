package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;

/** 越权解除待处置：只有质量经理能解除，其他岗位直接拒绝（403）。 */
public class ReleaseForbiddenException extends BizException {
  public ReleaseForbiddenException(String role) {
    super(ErrorCodes.RELEASE_FORBIDDEN, ErrorMessages.RELEASE_FORBIDDEN + ": " + role, 403);
  }
}

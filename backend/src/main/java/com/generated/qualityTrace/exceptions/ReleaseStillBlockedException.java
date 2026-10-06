package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;

/** 仍有未处置的严重不良，批次必须停在待处置（409）。 */
public class ReleaseStillBlockedException extends BizException {
  public ReleaseStillBlockedException(long openCriticalCount) {
    super(ErrorCodes.RELEASE_STILL_BLOCKED,
        ErrorMessages.RELEASE_STILL_BLOCKED + ": openCritical=" + openCriticalCount, 409);
  }
}

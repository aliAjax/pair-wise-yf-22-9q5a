package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;

/** 批次不存在。 */
public class BatchNotFoundException extends BizException {
  public BatchNotFoundException(String batchNo) {
    super(ErrorCodes.BATCH_NOT_FOUND, ErrorMessages.BATCH_NOT_FOUND + ": " + batchNo, 404);
  }
}

package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.exceptions.InvalidPayloadException;
import com.generated.qualityTrace.types.ReleaseSubmissionPayload;

public final class ReleaseSubmissionValidator {
  private ReleaseSubmissionValidator() {}

  public static void validate(String batchNo, ReleaseSubmissionPayload payload) {
    if (batchNo == null || batchNo.isBlank()) {
      throw new InvalidPayloadException("batchNo 不能为空");
    }
    if (payload != null && payload.note() != null && payload.note().length() > 500) {
      throw new InvalidPayloadException("note 长度不能超过 500");
    }
  }
}

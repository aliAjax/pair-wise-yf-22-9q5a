package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.exceptions.InvalidPayloadException;
import com.generated.qualityTrace.types.DefectDispositionPayload;
import com.generated.qualityTrace.types.DefectRecordPayload;

public final class DefectRecordValidator {
  private DefectRecordValidator() {}

  public static void validateCreate(DefectRecordPayload p) {
    if (p == null) {
      throw new InvalidPayloadException("请求体不能为空");
    }
    if (p.batchId() == null) {
      throw new InvalidPayloadException("batchId 不能为空");
    }
    if (p.defectType() == null || p.defectType().isBlank()) {
      throw new InvalidPayloadException("defectType 不能为空");
    }
    if (p.defectQty() == null || p.defectQty() < 1) {
      throw new InvalidPayloadException("defectQty 必须 >= 1");
    }
    requireEnum(DefectSeverity.class, p.severity(), "severity");
  }

  public static void validateDisposition(DefectDispositionPayload p) {
    if (p == null) {
      throw new InvalidPayloadException("请求体不能为空");
    }
    DefectDispositionStatus status = requireEnum(
        DefectDispositionStatus.class, p.dispositionStatus(), "dispositionStatus");
    if (DefectDispositionStatus.OPEN == status) {
      throw new InvalidPayloadException("处置动作不能把不良记录改回 OPEN");
    }
  }

  private static <E extends Enum<E>> E requireEnum(Class<E> type, String value, String field) {
    if (value == null || value.isBlank()) {
      throw new InvalidPayloadException(field + " 不能为空");
    }
    try {
      return Enum.valueOf(type, value);
    } catch (IllegalArgumentException e) {
      throw new InvalidPayloadException(field + " 非法取值: " + value);
    }
  }
}

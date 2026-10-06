package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.types.DefectRecordPayload;
import com.generated.qualityTrace.types.DispositionPayload;
import org.springframework.stereotype.Component;

/**
 * 不良记录入参校验器。
 */
@Component
public class DefectRecordValidator {

  public void validateRegister(DefectRecordPayload payload) {
    if (payload == null) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "不良请求体为空"));
    }
    if (payload.batchId() == null) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "必须指定批次"));
    }
    if (payload.defectType() == null || payload.defectType().isBlank()) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "不良类型不能为空"));
    }
    validateSeverity(payload.severity());
    if (payload.defectQty() != null && payload.defectQty() < 0) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "不良数量不能为负"));
    }
  }

  public DefectSeverity validateSeverity(String raw) {
    try {
      return DefectSeverity.from(raw);
    } catch (IllegalArgumentException e) {
      throw BusinessException.of(400, ErrorCodes.DEFECT_SEVERITY_INVALID,
          String.format(ErrorMessages.DEFECT_SEVERITY_INVALID, raw));
    }
  }

  public DefectDispositionStatus validateDisposition(DispositionPayload payload) {
    if (payload == null || payload.dispositionStatus() == null || payload.dispositionStatus().isBlank()) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "处置状态不能为空"));
    }
    DefectDispositionStatus status = DefectDispositionStatus.from(payload.dispositionStatus());
    if (status == null) {
      throw BusinessException.of(400, ErrorCodes.DEFECT_DISPOSITION_INVALID,
          String.format(ErrorMessages.DEFECT_DISPOSITION_INVALID, payload.dispositionStatus()));
    }
    return status;
  }
}

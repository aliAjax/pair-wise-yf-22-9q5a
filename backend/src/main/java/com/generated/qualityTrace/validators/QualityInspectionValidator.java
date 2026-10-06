package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.types.InspectionItemResultPayload;
import com.generated.qualityTrace.types.QualityInspectionPayload;
import org.springframework.stereotype.Component;

/**
 * 质量检验入参校验器。
 */
@Component
public class QualityInspectionValidator {

  public void validateSubmit(QualityInspectionPayload payload) {
    if (payload == null) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "检验请求体为空"));
    }
    if (payload.batchId() == null) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "必须指定批次"));
    }
    if (payload.inspectionType() == null || payload.inspectionType().isBlank()) {
      throw BusinessException.of(400, ErrorCodes.INSPECTION_TYPE_INVALID,
          String.format(ErrorMessages.INSPECTION_TYPE_INVALID, payload.inspectionType()));
    }
    validateResult(payload.resultStatus());
    if (payload.items() == null || payload.items().isEmpty()) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "至少录入一个检验项"));
    }
    for (InspectionItemResultPayload item : payload.items()) {
      if (item.itemCode() == null || item.itemCode().isBlank()) {
        throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
            String.format(ErrorMessages.VALIDATION_FAILED, "检验项编码不能为空"));
      }
    }
  }

  public InspectionResultStatus validateResult(String raw) {
    try {
      return InspectionResultStatus.from(raw);
    } catch (IllegalArgumentException e) {
      throw BusinessException.of(400, ErrorCodes.INSPECTION_RESULT_INVALID,
          String.format(ErrorMessages.INSPECTION_RESULT_INVALID, raw));
    }
  }
}

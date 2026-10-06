package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.types.ProductBatchPayload;
import org.springframework.stereotype.Component;

/**
 * 批次入参校验器。
 */
@Component
public class ProductBatchValidator {

  public void validateCreate(ProductBatchPayload payload) {
    if (payload == null) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "批次请求体为空"));
    }
    if (payload.batchNo() == null || payload.batchNo().isBlank()) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "批次号不能为空"));
    }
    if (payload.workOrderId() == null) {
      throw BusinessException.of(400, ErrorCodes.BATCH_WORK_ORDER_REQUIRED,
          ErrorMessages.BATCH_WORK_ORDER_REQUIRED);
    }
    if (payload.quantity() != null && payload.quantity() < 0) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "批次数量不能为负"));
    }
  }
}

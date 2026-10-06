package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.types.WorkOrderPayload;
import org.springframework.stereotype.Component;

/**
 * 工单入参校验器。
 */
@Component
public class WorkOrderValidator {

  public void validateCreate(WorkOrderPayload payload) {
    if (payload == null) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "工单请求体为空"));
    }
    if (payload.orderNo() == null || payload.orderNo().isBlank()) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "工单号不能为空"));
    }
    validateProductCode(payload.productCode());
    if (payload.plannedQty() != null && payload.plannedQty() < 0) {
      throw BusinessException.of(400, ErrorCodes.VALIDATION_FAILED,
          String.format(ErrorMessages.VALIDATION_FAILED, "计划数量不能为负"));
    }
  }

  public void validateProductCode(String productCode) {
    if (productCode == null || productCode.isBlank() || !productCode.matches("^[A-Za-z0-9\\-]+$")) {
      throw BusinessException.of(400, ErrorCodes.WORK_ORDER_PRODUCT_CODE_INVALID,
          String.format(ErrorMessages.WORK_ORDER_PRODUCT_CODE_INVALID, productCode));
    }
  }

  public WorkOrderStatus validateStatus(String raw) {
    try {
      return WorkOrderStatus.from(raw);
    } catch (IllegalArgumentException e) {
      throw BusinessException.of(400, ErrorCodes.WORK_ORDER_STATUS_INVALID,
          String.format(ErrorMessages.WORK_ORDER_STATUS_INVALID, raw));
    }
  }
}

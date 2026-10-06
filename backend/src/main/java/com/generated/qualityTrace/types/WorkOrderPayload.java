package com.generated.qualityTrace.types;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 工单创建 / 变更表单对象。
 */
public record WorkOrderPayload(
    @NotBlank(message = "工单号不能为空")
    String orderNo,

    @NotBlank(message = "产品编码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "产品编码只能包含字母、数字、短横线")
    String productCode,

    String productName,
    Integer plannedQty,
    String lineCode,
    String startAt,
    String status
) {
}

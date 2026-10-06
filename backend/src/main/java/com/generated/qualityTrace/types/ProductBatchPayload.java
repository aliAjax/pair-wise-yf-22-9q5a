package com.generated.qualityTrace.types;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 批次创建表单对象。
 */
public record ProductBatchPayload(
    @NotBlank(message = "批次号不能为空")
    String batchNo,

    @NotNull(message = "必须指定工单")
    Long workOrderId,

    Integer quantity,
    String materialLotNo,
    String producedAt
) {
}

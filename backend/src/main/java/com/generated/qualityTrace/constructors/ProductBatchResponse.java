package com.generated.qualityTrace.constructors;

/**
 * 批次响应对象。
 */
public record ProductBatchResponse(
    Long id,
    String batchNo,
    Long workOrderId,
    Integer quantity,
    String materialLotNo,
    String producedAt,
    String batchStatus,
    String batchStatusLabel
) {
}

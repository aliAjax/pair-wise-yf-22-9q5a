package com.generated.qualityTrace.constructors;

/**
 * 工单响应对象。
 */
public record WorkOrderResponse(
    Long id,
    String orderNo,
    String productCode,
    String productName,
    Integer plannedQty,
    String lineCode,
    String startAt,
    String status,
    String statusLabel
) {
}

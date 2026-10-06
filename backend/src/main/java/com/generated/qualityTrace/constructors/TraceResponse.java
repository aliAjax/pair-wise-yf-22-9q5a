package com.generated.qualityTrace.constructors;

import java.util.List;

/**
 * 批次全链路追溯树响应对象。
 */
public record TraceResponse(
    ProductBatchResponse batch,
    WorkOrderResponse workOrder,
    List<QualityInspectionResponse> inspections,
    List<DefectRecordResponse> defects,
    ReleaseConclusionResponse conclusion,
    String chainStatus
) {
}

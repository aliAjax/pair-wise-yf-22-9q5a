package com.generated.qualityTrace.constructors;

import java.util.List;

/**
 * 质量检验响应对象，含检验项结果列表。
 */
public record QualityInspectionResponse(
    Long id,
    Long batchId,
    Long inspectorId,
    String inspectionType,
    String standardVersion,
    String resultStatus,
    String resultStatusLabel,
    String reviewStatus,
    String reviewStatusLabel,
    String inspectedAt,
    List<InspectionItemResultResponse> items
) {
}

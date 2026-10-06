package com.generated.qualityTrace.constructors;

/**
 * 不良记录响应对象。
 */
public record DefectRecordResponse(
    Long id,
    Long batchId,
    String defectType,
    Integer defectQty,
    String severity,
    String severityLabel,
    String riskLevel,
    Boolean blocking,
    String rootCause,
    String dispositionStatus,
    String dispositionLabel,
    String createdAt,
    String updatedAt
) {
}

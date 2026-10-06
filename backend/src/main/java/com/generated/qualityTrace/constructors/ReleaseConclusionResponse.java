package com.generated.qualityTrace.constructors;

/**
 * 批次放行结论响应对象。
 */
public record ReleaseConclusionResponse(
    Long id,
    Long batchId,
    String conclusionStatus,
    String conclusionStatusLabel,
    Boolean valid,
    Long effectiveFinalInspectionId,
    Integer blockingDefectCount,
    String computedAt,
    String releasedAt,
    String releasedBy,
    Boolean releasable
) {
}

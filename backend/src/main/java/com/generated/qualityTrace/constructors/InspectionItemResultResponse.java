package com.generated.qualityTrace.constructors;

/**
 * 检验项结果响应对象。
 */
public record InspectionItemResultResponse(
    Long id,
    Long inspectionId,
    String itemCode,
    String itemName,
    String measuredValue,
    String limitMin,
    String limitMax,
    String itemStatus,
    String itemStatusLabel
) {
}

package com.generated.qualityTrace.types;

/** 登记不良请求体。 */
public record DefectRecordPayload(Long batchId, String defectType, Integer defectQty,
                                  String severity, String rootCause) {}

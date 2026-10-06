package com.generated.qualityTrace.types;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 不良登记表单对象。
 */
public record DefectRecordPayload(
    @NotNull(message = "必须指定批次")
    Long batchId,

    @NotBlank(message = "不良类型不能为空")
    String defectType,

    Integer defectQty,

    @NotBlank(message = "严重等级不能为空")
    String severity,

    String rootCause,
    String dispositionStatus
) {
}

package com.generated.qualityTrace.types;

import jakarta.validation.constraints.NotBlank;

/**
 * 不良处置表单对象（处置 / 关闭）。
 */
public record DispositionPayload(
    @NotBlank(message = "处置状态不能为空")
    String dispositionStatus,

    String rootCause
) {
}

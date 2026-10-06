package com.generated.qualityTrace.types;

import jakarta.validation.constraints.NotBlank;

/**
 * 检验项录入表单对象。
 */
public record InspectionItemResultPayload(
    @NotBlank(message = "检验项编码不能为空")
    String itemCode,

    String itemName,
    String measuredValue,
    String limitMin,
    String limitMax
) {
}

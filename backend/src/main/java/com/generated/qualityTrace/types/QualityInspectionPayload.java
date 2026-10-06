package com.generated.qualityTrace.types;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 检验提交表单对象，逐项写入检验结果。
 */
public record QualityInspectionPayload(
    @NotNull(message = "必须指定批次")
    Long batchId,

    @NotBlank(message = "检验类型不能为空")
    String inspectionType,

    String standardVersion,

    @NotBlank(message = "检验结果不能为空")
    String resultStatus,

    @NotEmpty(message = "至少录入一个检验项")
    @Valid
    List<InspectionItemResultPayload> items
) {
}

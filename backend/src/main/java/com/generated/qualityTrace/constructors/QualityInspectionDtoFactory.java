package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.utils.Formatters;
import java.util.Collections;
import java.util.List;

/**
 * 质量检验 DTO 构造器。
 */
public final class QualityInspectionDtoFactory {

  private QualityInspectionDtoFactory() {
  }

  public static QualityInspectionResponse from(QualityInspection entity, List<InspectionItemResult> items) {
    if (entity == null) {
      return null;
    }
    List<InspectionItemResultResponse> itemResponses = items == null
        ? Collections.emptyList()
        : items.stream().map(InspectionItemResultDtoFactory::from).toList();
    return new QualityInspectionResponse(
        entity.getId(),
        entity.getBatchId(),
        entity.getInspectorId(),
        entity.getInspectionType(),
        entity.getStandardVersion(),
        entity.getResultStatus(),
        Formatters.inspectionResultLabel(entity.getResultStatus()),
        entity.getReviewStatus(),
        Formatters.reviewStatusLabel(entity.getReviewStatus()),
        Formatters.formatDateTime(entity.getInspectedAt()),
        itemResponses
    );
  }

  /**
   * 默认检验单对象：默认生效，结果待录。
   */
  public static QualityInspection empty() {
    QualityInspection inspection = new QualityInspection();
    inspection.setReviewStatus("EFFECTIVE");
    inspection.setResultStatus("PENDING");
    return inspection;
  }
}

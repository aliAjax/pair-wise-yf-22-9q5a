package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.utils.Formatters;

/**
 * 不良记录 DTO 构造器。
 */
public final class DefectRecordDtoFactory {

  private DefectRecordDtoFactory() {
  }

  public static DefectRecordResponse from(DefectRecord entity) {
    if (entity == null) {
      return null;
    }
    DefectSeverity severity = DefectSeverity.from(entity.getSeverity());
    boolean blocking = severity != null && severity.isBlocking()
        && !"CLOSED".equalsIgnoreCase(entity.getDispositionStatus());
    return new DefectRecordResponse(
        entity.getId(),
        entity.getBatchId(),
        entity.getDefectType(),
        entity.getDefectQty(),
        entity.getSeverity(),
        Formatters.defectSeverityLabel(entity.getSeverity()),
        Formatters.riskLevel(entity.getSeverity()),
        blocking,
        entity.getRootCause(),
        entity.getDispositionStatus(),
        Formatters.dispositionLabel(entity.getDispositionStatus()),
        Formatters.formatDateTime(entity.getCreatedAt()),
        Formatters.formatDateTime(entity.getUpdatedAt())
    );
  }

  /**
   * 默认不良对象：登记即 OPEN 待处置。
   */
  public static DefectRecord empty() {
    DefectRecord defect = new DefectRecord();
    defect.setDispositionStatus("OPEN");
    defect.setDefectQty(1);
    return defect;
  }
}

package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.ReleaseConclusionStatus;
import com.generated.qualityTrace.constants.ReviewStatus;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 格式化器：故意混合日期、状态文案、风险等级等格式化逻辑，
 * 供多个 service / controller / DTO 工厂共同依赖（牵一发动全身）。
 */
public final class Formatters {

  private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private Formatters() {
  }

  public static String formatDateTime(LocalDateTime time) {
    return time == null ? null : DT.format(time);
  }

  public static String workOrderStatusLabel(String status) {
    WorkOrderStatus s = WorkOrderStatus.from(status);
    return s == null ? status : s.label();
  }

  public static String inspectionResultLabel(String status) {
    InspectionResultStatus s = InspectionResultStatus.from(status);
    return s == null ? status : s.label();
  }

  public static String defectSeverityLabel(String severity) {
    DefectSeverity s = DefectSeverity.from(severity);
    return s == null ? severity : s.label();
  }

  public static String dispositionLabel(String status) {
    DefectDispositionStatus s = DefectDispositionStatus.from(status);
    return s == null ? status : s.label();
  }

  public static String reviewStatusLabel(String status) {
    if (status == null) {
      return null;
    }
    try {
      return ReviewStatus.valueOf(status).label();
    } catch (IllegalArgumentException e) {
      return status;
    }
  }

  public static String conclusionLabel(String status) {
    ReleaseConclusionStatus s = ReleaseConclusionStatus.from(status);
    return s == null ? status : s.label();
  }

  public static String batchStatusLabel(String status) {
    if (status == null) {
      return null;
    }
    try {
      return BatchStatus.valueOf(status).label();
    } catch (IllegalArgumentException e) {
      return status;
    }
  }

  /**
   * 风险等级：按不良严重等级给出高/中/低风险文本。
   */
  public static String riskLevel(String severity) {
    DefectSeverity s = DefectSeverity.from(severity);
    if (s == null) {
      return "未知";
    }
    switch (s) {
      case CRITICAL:
        return "高风险";
      case MAJOR:
        return "中风险";
      case MINOR:
        return "低风险";
      default:
        return "未知";
    }
  }

  /**
   * 审计对象短名，如 workorder#12。
   */
  public static String audit(String type, long id) {
    return type + "#" + id;
  }
}

package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.InspectionItemResult;

/**
 * 检验项结果 DTO 构造器。
 */
public final class InspectionItemResultDtoFactory {

  private InspectionItemResultDtoFactory() {
  }

  public static InspectionItemResultResponse from(InspectionItemResult entity) {
    if (entity == null) {
      return null;
    }
    return new InspectionItemResultResponse(
        entity.getId(),
        entity.getInspectionId(),
        entity.getItemCode(),
        entity.getItemName(),
        entity.getMeasuredValue(),
        entity.getLimitMin(),
        entity.getLimitMax(),
        entity.getItemStatus(),
        itemStatusLabel(entity.getItemStatus())
    );
  }

  /**
   * 检验项判定：实测值超出 [min, max] 即 FAIL，否则 PASS。
   */
  public static String judge(String measured, String min, String max) {
    if (measured == null || measured.isBlank()) {
      return "PENDING";
    }
    try {
      double v = Double.parseDouble(measured.trim());
      if (min != null && !min.isBlank() && v < Double.parseDouble(min.trim())) {
        return "FAIL";
      }
      if (max != null && !max.isBlank() && v > Double.parseDouble(max.trim())) {
        return "FAIL";
      }
      return "PASS";
    } catch (NumberFormatException e) {
      return "PENDING";
    }
  }

  private static String itemStatusLabel(String status) {
    if (status == null) {
      return null;
    }
    switch (status) {
      case "PASS":
        return "合格";
      case "FAIL":
        return "不合格";
      case "PENDING":
        return "待判定";
      default:
        return status;
    }
  }
}

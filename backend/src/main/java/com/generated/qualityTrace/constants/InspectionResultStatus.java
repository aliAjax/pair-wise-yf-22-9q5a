package com.generated.qualityTrace.constants;

/**
 * 检验结果状态。出现位置：model、DTO 构造器、日志模板、错误消息、service、controller、校验器。
 */
public enum InspectionResultStatus {
  PASS("合格"),
  FAIL("不合格"),
  CONDITIONAL_PASS("让步接收"),
  RECHECK("复检");

  private final String label;

  InspectionResultStatus(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  public static InspectionResultStatus from(String raw) {
    if (raw == null) {
      return null;
    }
    for (InspectionResultStatus s : values()) {
      if (s.name().equalsIgnoreCase(raw.trim())) {
        return s;
      }
    }
    throw new IllegalArgumentException("unknown inspection result status: " + raw);
  }
}

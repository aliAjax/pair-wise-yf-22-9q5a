package com.generated.qualityTrace.constants;

/**
 * 不良严重等级。出现位置：model、DTO 构造器、日志模板、错误消息、service、controller、校验器。
 *
 * <p>MAJOR / CRITICAL 属于阻断放行的严重不良，只要存在未处置记录，批次不得放行。</p>
 */
public enum DefectSeverity {
  MINOR("轻微"),
  MAJOR("严重"),
  CRITICAL("致命");

  private final String label;

  DefectSeverity(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  /**
   * 是否为阻断放行的严重不良。
   */
  public boolean isBlocking() {
    return this == MAJOR || this == CRITICAL;
  }

  public static DefectSeverity from(String raw) {
    if (raw == null) {
      return null;
    }
    for (DefectSeverity s : values()) {
      if (s.name().equalsIgnoreCase(raw.trim())) {
        return s;
      }
    }
    throw new IllegalArgumentException("unknown defect severity: " + raw);
  }
}

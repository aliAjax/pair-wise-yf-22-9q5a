package com.generated.qualityTrace.constants;

/**
 * 不良处置状态。只有 CLOSED 才算处置完成，不再阻断放行。
 */
public enum DefectDispositionStatus {
  OPEN("待处置"),
  IN_PROGRESS("处置中"),
  CLOSED("已关闭");

  private final String label;

  DefectDispositionStatus(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  /**
   * 是否仍未处置完成（OPEN / IN_PROGRESS 都会阻断放行）。
   */
  public boolean isOpen() {
    return this != CLOSED;
  }

  public static DefectDispositionStatus from(String raw) {
    if (raw == null) {
      return OPEN;
    }
    for (DefectDispositionStatus s : values()) {
      if (s.name().equalsIgnoreCase(raw.trim())) {
        return s;
      }
    }
    return OPEN;
  }
}

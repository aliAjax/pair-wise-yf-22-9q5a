package com.generated.qualityTrace.constants;

/**
 * 批次状态。由放行结论联动：存在未处置严重不良时为 HOLD_FOR_DISPOSITION，
 * 放行后为 RELEASED，生产中为 IN_PROGRESS。
 */
public enum BatchStatus {
  IN_PROGRESS("生产中"),
  PENDING_FINAL_INSPECTION("待终检"),
  HOLD_FOR_DISPOSITION("待处置"),
  RELEASED("已放行");

  private final String label;

  BatchStatus(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }
}

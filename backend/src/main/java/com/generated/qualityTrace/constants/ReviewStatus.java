package com.generated.qualityTrace.constants;

/**
 * 检验单复核状态。
 *
 * <p>同一批次同一检验类型只允许一张 EFFECTIVE（生效）检验单；质检员与产线主管
 * 同时提交时，先到的置为 EFFECTIVE，晚到的置为 PENDING_REVIEW（待复核），
 * 不覆盖已有结论。</p>
 */
public enum ReviewStatus {
  EFFECTIVE("生效"),
  PENDING_REVIEW("待复核");

  private final String label;

  ReviewStatus(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }
}

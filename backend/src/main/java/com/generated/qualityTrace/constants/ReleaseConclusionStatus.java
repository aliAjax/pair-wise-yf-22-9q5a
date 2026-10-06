package com.generated.qualityTrace.constants;

/**
 * 批次放行结论状态。
 *
 * <ul>
 *   <li>PENDING_FINAL_INSPECTION：尚未有生效终检，无法放行；</li>
 *   <li>HOLD_FOR_DISPOSITION：存在未处置严重不良或终检未过，批次停在待处置；</li>
 *   <li>RELEASED：终检合格且无未处置严重不良，质量经理已放行。</li>
 * </ul>
 */
public enum ReleaseConclusionStatus {
  PENDING_FINAL_INSPECTION("待终检"),
  HOLD_FOR_DISPOSITION("待处置"),
  RELEASED("已放行");

  private final String label;

  ReleaseConclusionStatus(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  public static ReleaseConclusionStatus from(String raw) {
    if (raw == null) {
      return null;
    }
    for (ReleaseConclusionStatus s : values()) {
      if (s.name().equalsIgnoreCase(raw.trim())) {
        return s;
      }
    }
    throw new IllegalArgumentException("unknown release conclusion status: " + raw);
  }
}

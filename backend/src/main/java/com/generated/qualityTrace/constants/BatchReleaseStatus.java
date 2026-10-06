package com.generated.qualityTrace.constants;

/** 批次放行结论状态：放行链（工单 -> 批次 -> 终检 -> 不良）算出的当前结论。 */
public enum BatchReleaseStatus {
  /** 待放行：链路未齐（终检未做或未通过）。 */
  PENDING_RELEASE,
  /** 已放行：终检通过且无未处置严重不良。 */
  RELEASED,
  /** 待处置：存在未处置的严重不良，批次被锁定，只能由质量经理解除。 */
  PENDING_DISPOSITION
}

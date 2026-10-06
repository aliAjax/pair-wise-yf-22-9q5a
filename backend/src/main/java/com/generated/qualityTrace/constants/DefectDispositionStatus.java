package com.generated.qualityTrace.constants;

/** 不良记录处置状态。严重不良（CRITICAL）只要还处于 OPEN，批次就停在待处置。 */
public enum DefectDispositionStatus {
  /** 未处置。 */
  OPEN,
  /** 已处置。 */
  DISPOSED,
  /** 已关闭。 */
  CLOSED
}

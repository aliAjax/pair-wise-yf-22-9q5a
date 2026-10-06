package com.generated.qualityTrace.constants;

/** 放行提交结果：同一批次同一放行周期内，先到的生效，晚到的留在待复核。 */
public enum ReleaseSubmissionOutcome {
  /** 生效：本周期内第一个到达的提交，驱动放行结论重算。 */
  APPLIED,
  /** 待复核：晚到的提交，不覆盖已生效结论，等待复核。 */
  PENDING_RECHECK,
  /** 已拒绝：越权或非法提交被直接拒绝。 */
  REJECTED
}

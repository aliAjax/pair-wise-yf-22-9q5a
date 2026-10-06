package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.constants.BatchReleaseStatus;
import com.generated.qualityTrace.constants.ReleaseSubmissionOutcome;

public final class Formatters {
  private Formatters() {}

  public static String audit(String type, long id) {
    return type + "#" + id;
  }

  /** 放行结论状态文案。 */
  public static String releaseStatusText(String status) {
    if (status == null) {
      return "未知";
    }
    if (BatchReleaseStatus.RELEASED.name().equals(status)) {
      return "已放行";
    }
    if (BatchReleaseStatus.PENDING_DISPOSITION.name().equals(status)) {
      return "待处置";
    }
    if (BatchReleaseStatus.PENDING_RELEASE.name().equals(status)) {
      return "待放行";
    }
    return status;
  }

  /** 放行提交结果文案。 */
  public static String submissionOutcomeText(String outcome) {
    if (outcome == null) {
      return "未知";
    }
    if (ReleaseSubmissionOutcome.APPLIED.name().equals(outcome)) {
      return "生效";
    }
    if (ReleaseSubmissionOutcome.PENDING_RECHECK.name().equals(outcome)) {
      return "待复核";
    }
    if (ReleaseSubmissionOutcome.REJECTED.name().equals(outcome)) {
      return "已拒绝";
    }
    return outcome;
  }
}

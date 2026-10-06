package com.generated.qualityTrace.models;

/** 放行提交：同一批次同一周期内先到生效（APPLIED），晚到留待复核（PENDING_RECHECK）。 */
public class ReleaseSubmission {
  public Long id;
  public String batchNo;
  /** 提交时所处的放行周期。 */
  public int cycle;
  public String actorId;
  /** 取值见 constants.RoleCode。 */
  public String role;
  /** 取值见 constants.ReleaseSubmissionOutcome。 */
  public String outcome;
  public String note;
  public String submittedAt;

  public ReleaseSubmission() {}

  public ReleaseSubmission(Long id, String batchNo, int cycle, String actorId,
                           String role, String outcome, String note, String submittedAt) {
    this.id = id;
    this.batchNo = batchNo;
    this.cycle = cycle;
    this.actorId = actorId;
    this.role = role;
    this.outcome = outcome;
    this.note = note;
    this.submittedAt = submittedAt;
  }
}

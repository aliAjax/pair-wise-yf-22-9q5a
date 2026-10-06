package com.generated.qualityTrace.models;

/** 放行结论：按批次缓存的计算结果。不良记录一变即作废（stale=true）并重算。 */
public class BatchRelease {
  public Long id;
  public String batchNo;
  public Long workOrderId;
  /** 取值见 constants.BatchReleaseStatus。 */
  public String status;
  /** 结论说明，例如“存在 2 条未处置严重不良”。 */
  public String reason;
  /** 放行周期：不良记录每次变更 +1，提交按周期判先后。 */
  public int cycle;
  /** 作废标记：true 表示结论已失效，下次读取必须重算。 */
  public boolean stale;
  public String computedAt;

  public BatchRelease() {}

  public BatchRelease(Long id, String batchNo, Long workOrderId, String status,
                      String reason, int cycle, boolean stale, String computedAt) {
    this.id = id;
    this.batchNo = batchNo;
    this.workOrderId = workOrderId;
    this.status = status;
    this.reason = reason;
    this.cycle = cycle;
    this.stale = stale;
    this.computedAt = computedAt;
  }
}

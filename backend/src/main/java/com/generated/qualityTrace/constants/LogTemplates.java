package com.generated.qualityTrace.constants;

/**
 * 日志模板集中管理。每个实体至少 4 条，所有写操作都要记录日志；
 * 字段变更时必须同步改日志模板和调用处。
 *
 * <p>约定：模板使用 {} 作为 SLF4J 占位符，运行时由日志框架填充。</p>
 */
public final class LogTemplates {

  private LogTemplates() {
  }

  // 认证 / 鉴权
  public static final String AUTH_LOGIN_SUCCESS = "auth.login.success username={} role={}";
  public static final String AUTH_LOGIN_FAILED = "auth.login.failed username={} reason={}";
  public static final String AUTH_TOKEN_VALIDATED = "auth.token.validated username={} role={}";
  public static final String AUTH_ACCESS_DENIED = "auth.access.denied username={} role={} path={} required={}";

  // 工单 WorkOrder
  public static final String WO_CREATED = "workorder.created id={} orderNo={} productCode={}";
  public static final String WO_STATUS_CHANGED = "workorder.status.changed id={} {} -> {}";
  public static final String WO_PROGRESS_UPDATED = "workorder.progress.updated id={} plannedQty={} lineCode={}";
  public static final String WO_VALIDATION_FAILED = "workorder.validation.failed orderNo={} reason={}";
  public static final String WO_NOT_FOUND = "workorder.not.found key={}";

  // 批次 ProductBatch
  public static final String BATCH_CREATED = "batch.created id={} batchNo={} workOrderId={}";
  public static final String BATCH_STATUS_CHANGED = "batch.status.changed id={} {} -> {}";
  public static final String BATCH_HOLD = "batch.hold id={} batchNo={} blockingDefects={}";
  public static final String BATCH_RELEASED = "batch.released id={} batchNo={} releasedBy={}";
  public static final String BATCH_NOT_FOUND = "batch.not.found key={}";

  // 放行链 ReleaseConclusion
  public static final String CONCLUSION_INVALIDATED = "conclusion.invalidated batchId={} reason={}";
  public static final String CONCLUSION_RECOMPUTED = "conclusion.recomputed batchId={} status={} blocking={} finalInspectionId={}";
  public static final String CONCLUSION_RELEASE_BLOCKED = "conclusion.release.blocked batchId={} status={}";
  public static final String CONCLUSION_LIFT_REQUESTED = "conclusion.lift.requested batchId={} operator={}";

  // 检验 QualityInspection
  public static final String INSPECTION_SUBMITTED = "inspection.submitted id={} batchId={} type={} result={}";
  public static final String INSPECTION_EFFECTIVE = "inspection.effective id={} batchId={} type={}";
  public static final String INSPECTION_PENDING_REVIEW = "inspection.pending.review id={} batchId={} type={} reason=concurrent-first-write-wins";
  public static final String INSPECTION_ITEMS_RECORDED = "inspection.items.recorded inspectionId={} itemCount={}";
  public static final String INSPECTION_NOT_FOUND = "inspection.not.found key={}";

  // 检验项 InspectionItemResult
  public static final String ITEM_RECORDED = "inspection.item.recorded id={} inspectionId={} itemCode={} status={}";
  public static final String ITEM_JUDGED = "inspection.item.judged id={} itemCode={} measured={} result={}";
  public static final String ITEM_BATCH_SAVED = "inspection.item.batch.saved inspectionId={} count={}";
  public static final String ITEM_OUT_OF_LIMIT = "inspection.item.out.of.limit itemCode={} measured={} min={} max={}";

  // 不良 DefectRecord
  public static final String DEFECT_REGISTERED = "defect.registered id={} batchId={} severity={} qty={}";
  public static final String DEFECT_UPDATED = "defect.updated id={} {} -> {}";
  public static final String DEFECT_CLOSED = "defect.closed id={} batchId={} disposition={}";
  public static final String DEFECT_SEVERITY_CLASSIFIED = "defect.severity.classified id={} severity={} blocking={}";
  public static final String DEFECT_NOT_FOUND = "defect.not.found key={}";

  // 追溯 Trace
  public static final String TRACE_QUERIED = "trace.queried batchNo={} operator={}";
  public static final String TRACE_CHAIN_BUILT = "trace.chain.built batchNo={} workOrderId={} inspections={} defects={} conclusion={}";

  // 通用
  public static final String RATE_LIMIT_TRIGGERED = "rate.limit.triggered ip={} path={}";
  public static final String VALIDATION_FAILED = "validation.failed path={} reason={}";
  public static final String AUDIT_WRITE = "audit.write actor={} action={} target={}/{}";
}

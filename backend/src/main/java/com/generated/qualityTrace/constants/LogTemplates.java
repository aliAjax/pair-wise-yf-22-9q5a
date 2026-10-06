package com.generated.qualityTrace.constants;

/** 日志模板：所有写操作必须记录日志，字段变更时同步改这里和调用处。 */
public final class LogTemplates {
  public static final String CREATE = "create";
  public static final String UPDATE = "update";
  public static final String STATUS = "status";
  public static final String EXPORT = "export";

  // 放行链（BatchRelease）
  public static final String RELEASE_COMPUTED = "release computed batchNo=%s status=%s cycle=%d openCritical=%d";
  public static final String RELEASE_INVALIDATED = "release invalidated batchNo=%s cycle=%d reason=%s";
  public static final String RELEASE_DEMOTED = "release demoted batchNo=%s from=%s to=%s";
  public static final String RELEASE_RESOLVED = "release resolved batchNo=%s by=%s status=%s";
  // 放行提交（ReleaseSubmission）
  public static final String SUBMIT_APPLIED = "submission applied batchNo=%s actor=%s role=%s cycle=%d";
  public static final String SUBMIT_RECHECK = "submission held for recheck batchNo=%s actor=%s role=%s cycle=%d";
  public static final String SUBMIT_REJECTED = "submission rejected batchNo=%s actor=%s role=%s reason=%s";
  // 不良记录（DefectRecord）
  public static final String DEFECT_REGISTERED = "defect registered id=%d batchId=%d severity=%s qty=%d";
  public static final String DEFECT_DISPOSED = "defect disposed id=%d batchId=%d disposition=%s";

  private LogTemplates() {}
}

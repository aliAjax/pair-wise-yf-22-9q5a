package com.generated.qualityTrace.models;

/** 不良记录：CRITICAL 且未处置（OPEN）的记录会把批次锁在待处置。 */
public class DefectRecord {
  public Long id;
  public Long batchId;
  public String defectType;
  public Integer defectQty;
  /** 取值见 constants.DefectSeverity。 */
  public String severity;
  public String rootCause;
  /** 取值见 constants.DefectDispositionStatus。 */
  public String dispositionStatus;
  public String createdAt;

  public DefectRecord() {}

  public DefectRecord(Long id, Long batchId, String defectType, Integer defectQty,
                      String severity, String rootCause, String dispositionStatus, String createdAt) {
    this.id = id;
    this.batchId = batchId;
    this.defectType = defectType;
    this.defectQty = defectQty;
    this.severity = severity;
    this.rootCause = rootCause;
    this.dispositionStatus = dispositionStatus;
    this.createdAt = createdAt;
  }
}

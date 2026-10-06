package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 不良记录。severity 为 MAJOR/CRITICAL 且 disposition_status 未关闭时阻断放行。
 */
@TableName("defect_record")
public class DefectRecord {

  @TableId(type = IdType.AUTO)
  private Long id;
  private Long batchId;
  private String defectType;
  private Integer defectQty;
  private String severity;
  private String rootCause;
  private String dispositionStatus;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getBatchId() {
    return batchId;
  }

  public void setBatchId(Long batchId) {
    this.batchId = batchId;
  }

  public String getDefectType() {
    return defectType;
  }

  public void setDefectType(String defectType) {
    this.defectType = defectType;
  }

  public Integer getDefectQty() {
    return defectQty;
  }

  public void setDefectQty(Integer defectQty) {
    this.defectQty = defectQty;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public String getRootCause() {
    return rootCause;
  }

  public void setRootCause(String rootCause) {
    this.rootCause = rootCause;
  }

  public String getDispositionStatus() {
    return dispositionStatus;
  }

  public void setDispositionStatus(String dispositionStatus) {
    this.dispositionStatus = dispositionStatus;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}

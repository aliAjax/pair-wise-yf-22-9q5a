package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 质量检验单。review_status=EFFECTIVE 为生效结论，PENDING_REVIEW 为待复核（晚到的并发提交）。
 */
@TableName("quality_inspection")
public class QualityInspection {

  @TableId(type = IdType.AUTO)
  private Long id;
  private Long batchId;
  private Long inspectorId;
  private String inspectionType;
  private String standardVersion;
  private String resultStatus;
  private String reviewStatus;
  private LocalDateTime inspectedAt;

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

  public Long getInspectorId() {
    return inspectorId;
  }

  public void setInspectorId(Long inspectorId) {
    this.inspectorId = inspectorId;
  }

  public String getInspectionType() {
    return inspectionType;
  }

  public void setInspectionType(String inspectionType) {
    this.inspectionType = inspectionType;
  }

  public String getStandardVersion() {
    return standardVersion;
  }

  public void setStandardVersion(String standardVersion) {
    this.standardVersion = standardVersion;
  }

  public String getResultStatus() {
    return resultStatus;
  }

  public void setResultStatus(String resultStatus) {
    this.resultStatus = resultStatus;
  }

  public String getReviewStatus() {
    return reviewStatus;
  }

  public void setReviewStatus(String reviewStatus) {
    this.reviewStatus = reviewStatus;
  }

  public LocalDateTime getInspectedAt() {
    return inspectedAt;
  }

  public void setInspectedAt(LocalDateTime inspectedAt) {
    this.inspectedAt = inspectedAt;
  }
}

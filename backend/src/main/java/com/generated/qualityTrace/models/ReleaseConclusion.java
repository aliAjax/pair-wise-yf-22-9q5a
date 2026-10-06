package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 批次放行结论（计算结果缓存）。
 *
 * <p>该结论由放行链根据「生效终检结论 + 未处置严重不良数」算出。
 * 不良记录一旦变更，valid 置为 false（作废）并立即重算。</p>
 */
@TableName("release_conclusion")
public class ReleaseConclusion {

  @TableId(type = IdType.AUTO)
  private Long id;
  private Long batchId;
  private String conclusionStatus;
  private Boolean valid;
  private Long effectiveFinalInspectionId;
  private Integer blockingDefectCount;
  private LocalDateTime computedAt;
  private LocalDateTime releasedAt;
  private String releasedBy;

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

  public String getConclusionStatus() {
    return conclusionStatus;
  }

  public void setConclusionStatus(String conclusionStatus) {
    this.conclusionStatus = conclusionStatus;
  }

  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  public Long getEffectiveFinalInspectionId() {
    return effectiveFinalInspectionId;
  }

  public void setEffectiveFinalInspectionId(Long effectiveFinalInspectionId) {
    this.effectiveFinalInspectionId = effectiveFinalInspectionId;
  }

  public Integer getBlockingDefectCount() {
    return blockingDefectCount;
  }

  public void setBlockingDefectCount(Integer blockingDefectCount) {
    this.blockingDefectCount = blockingDefectCount;
  }

  public LocalDateTime getComputedAt() {
    return computedAt;
  }

  public void setComputedAt(LocalDateTime computedAt) {
    this.computedAt = computedAt;
  }

  public LocalDateTime getReleasedAt() {
    return releasedAt;
  }

  public void setReleasedAt(LocalDateTime releasedAt) {
    this.releasedAt = releasedAt;
  }

  public String getReleasedBy() {
    return releasedBy;
  }

  public void setReleasedBy(String releasedBy) {
    this.releasedBy = releasedBy;
  }
}

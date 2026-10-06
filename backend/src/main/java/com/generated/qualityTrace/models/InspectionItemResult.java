package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 检验项结果。item_status 由实测值与上下限判定。
 */
@TableName("inspection_item_result")
public class InspectionItemResult {

  @TableId(type = IdType.AUTO)
  private Long id;
  private Long inspectionId;
  private String itemCode;
  private String itemName;
  private String measuredValue;
  private String limitMin;
  private String limitMax;
  private String itemStatus;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getInspectionId() {
    return inspectionId;
  }

  public void setInspectionId(Long inspectionId) {
    this.inspectionId = inspectionId;
  }

  public String getItemCode() {
    return itemCode;
  }

  public void setItemCode(String itemCode) {
    this.itemCode = itemCode;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public String getMeasuredValue() {
    return measuredValue;
  }

  public void setMeasuredValue(String measuredValue) {
    this.measuredValue = measuredValue;
  }

  public String getLimitMin() {
    return limitMin;
  }

  public void setLimitMin(String limitMin) {
    this.limitMin = limitMin;
  }

  public String getLimitMax() {
    return limitMax;
  }

  public void setLimitMax(String limitMax) {
    this.limitMax = limitMax;
  }

  public String getItemStatus() {
    return itemStatus;
  }

  public void setItemStatus(String itemStatus) {
    this.itemStatus = itemStatus;
  }
}

package com.generated.qualityTrace.models;

/** 生产工单。 */
public class WorkOrder {
  public Long id;
  public String orderNo;
  public String productCode;
  public String productName;
  public Long plannedQty;
  public String lineCode;
  public String startAt;
  /** 取值见 constants.WorkOrderStatus。 */
  public String status;

  public WorkOrder() {}

  public WorkOrder(Long id, String orderNo, String productCode, String productName,
                   Long plannedQty, String lineCode, String startAt, String status) {
    this.id = id;
    this.orderNo = orderNo;
    this.productCode = productCode;
    this.productName = productName;
    this.plannedQty = plannedQty;
    this.lineCode = lineCode;
    this.startAt = startAt;
    this.status = status;
  }
}

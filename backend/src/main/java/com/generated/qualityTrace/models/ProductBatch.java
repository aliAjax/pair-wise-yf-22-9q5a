package com.generated.qualityTrace.models;

/** 产品批次：放行链的锚点，工单 -> 批次 -> 检验 -> 不良 -> 放行结论。 */
public class ProductBatch {
  public Long id;
  public String batchNo;
  public Long workOrderId;
  public Long quantity;
  public String materialLotNo;
  public String producedAt;
  /** 取值见 constants.BatchStatus。 */
  public String batchStatus;

  public ProductBatch() {}

  public ProductBatch(Long id, String batchNo, Long workOrderId, Long quantity,
                      String materialLotNo, String producedAt, String batchStatus) {
    this.id = id;
    this.batchNo = batchNo;
    this.workOrderId = workOrderId;
    this.quantity = quantity;
    this.materialLotNo = materialLotNo;
    this.producedAt = producedAt;
    this.batchStatus = batchStatus;
  }
}

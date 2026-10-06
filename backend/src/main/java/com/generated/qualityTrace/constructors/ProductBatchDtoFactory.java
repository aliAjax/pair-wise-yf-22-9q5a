package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.utils.Formatters;

/**
 * 批次 DTO 构造器。
 */
public final class ProductBatchDtoFactory {

  private ProductBatchDtoFactory() {
  }

  public static ProductBatchResponse from(ProductBatch entity) {
    if (entity == null) {
      return null;
    }
    return new ProductBatchResponse(
        entity.getId(),
        entity.getBatchNo(),
        entity.getWorkOrderId(),
        entity.getQuantity(),
        entity.getMaterialLotNo(),
        Formatters.formatDateTime(entity.getProducedAt()),
        entity.getBatchStatus(),
        Formatters.batchStatusLabel(entity.getBatchStatus())
    );
  }

  /**
   * 默认批次对象：新建即进入待终检。
   */
  public static ProductBatch empty() {
    ProductBatch batch = new ProductBatch();
    batch.setBatchStatus("PENDING_FINAL_INSPECTION");
    batch.setQuantity(0);
    return batch;
  }
}

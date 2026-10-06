package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.ProductBatch;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ProductBatchDtoFactory {
  private ProductBatchDtoFactory() {}

  public static Map<String, Object> toResponse(ProductBatch b) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", b.id);
    m.put("batchNo", b.batchNo);
    m.put("workOrderId", b.workOrderId);
    m.put("quantity", b.quantity);
    m.put("materialLotNo", b.materialLotNo);
    m.put("producedAt", b.producedAt);
    m.put("batchStatus", b.batchStatus);
    return m;
  }
}

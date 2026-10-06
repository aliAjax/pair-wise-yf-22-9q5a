package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.WorkOrder;
import java.util.LinkedHashMap;
import java.util.Map;

public final class WorkOrderDtoFactory {
  private WorkOrderDtoFactory() {}

  public static Map<String, Object> toResponse(WorkOrder o) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", o.id);
    m.put("orderNo", o.orderNo);
    m.put("productCode", o.productCode);
    m.put("productName", o.productName);
    m.put("plannedQty", o.plannedQty);
    m.put("lineCode", o.lineCode);
    m.put("startAt", o.startAt);
    m.put("status", o.status);
    return m;
  }
}

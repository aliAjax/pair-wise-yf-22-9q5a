package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.utils.Formatters;

/**
 * 工单 DTO 构造器：统一负责默认对象、表单对象与响应对象的构造。
 */
public final class WorkOrderDtoFactory {

  private WorkOrderDtoFactory() {
  }

  /**
   * 由实体构造响应对象。
   */
  public static WorkOrderResponse from(WorkOrder entity) {
    if (entity == null) {
      return null;
    }
    return new WorkOrderResponse(
        entity.getId(),
        entity.getOrderNo(),
        entity.getProductCode(),
        entity.getProductName(),
        entity.getPlannedQty(),
        entity.getLineCode(),
        Formatters.formatDateTime(entity.getStartAt()),
        entity.getStatus(),
        Formatters.workOrderStatusLabel(entity.getStatus())
    );
  }

  /**
   * 默认表单对象（新建工单时的空白结构）。
   */
  public static WorkOrder empty() {
    WorkOrder wo = new WorkOrder();
    wo.setStatus("PLANNED");
    wo.setPlannedQty(0);
    return wo;
  }
}

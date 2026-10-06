package com.generated.qualityTrace.constants;

/**
 * 工单状态。出现位置：model、DTO 构造器、日志模板、错误消息、service、controller、校验器。
 */
public enum WorkOrderStatus {
  PLANNED("已计划"),
  RUNNING("生产中"),
  PAUSED("已暂停"),
  FINISHED("已完工"),
  CANCELLED("已取消");

  private final String label;

  WorkOrderStatus(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }

  public static WorkOrderStatus from(String raw) {
    if (raw == null) {
      return null;
    }
    for (WorkOrderStatus s : values()) {
      if (s.name().equalsIgnoreCase(raw.trim())) {
        return s;
      }
    }
    throw new IllegalArgumentException("unknown work order status: " + raw);
  }
}

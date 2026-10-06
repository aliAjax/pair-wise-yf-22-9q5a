package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.WorkOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class WorkOrderRepository {
  private final ConcurrentHashMap<Long, WorkOrder> byId = new ConcurrentHashMap<>();
  private final AtomicLong seq = new AtomicLong(0);

  public WorkOrderRepository() {
    save(new WorkOrder(null, "WO-1001", "P-AX100", "轴套 AX100", 500L, "LINE-A",
        "2026-10-06T08:00:00Z", WorkOrderStatus.RUNNING.name()));
  }

  public Optional<WorkOrder> findById(Long id) {
    return Optional.ofNullable(byId.get(id));
  }

  public List<WorkOrder> findAll() {
    return new ArrayList<>(byId.values());
  }

  public WorkOrder save(WorkOrder order) {
    if (order.id == null) {
      order.id = seq.incrementAndGet();
    }
    byId.put(order.id, order);
    return order;
  }
}

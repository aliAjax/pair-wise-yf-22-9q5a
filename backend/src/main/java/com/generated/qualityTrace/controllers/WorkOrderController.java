package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.constructors.WorkOrderResponse;
import com.generated.qualityTrace.middlewares.RequireRole;
import com.generated.qualityTrace.routes.WorkOrderRoutes;
import com.generated.qualityTrace.services.WorkOrderService;
import com.generated.qualityTrace.types.WorkOrderPayload;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工单控制器。
 */
@RestController
@RequestMapping(WorkOrderRoutes.BASE)
public class WorkOrderController {

  private final WorkOrderService workOrderService;

  public WorkOrderController(WorkOrderService workOrderService) {
    this.workOrderService = workOrderService;
  }

  @PostMapping
  @RequireRole({UserRole.QUALITY_MANAGER, UserRole.LINE_SUPERVISOR})
  public WorkOrderResponse create(@Valid @RequestBody WorkOrderPayload payload) {
    return workOrderService.create(payload);
  }

  @GetMapping
  public List<WorkOrderResponse> list() {
    return workOrderService.list();
  }

  @GetMapping("/{id}")
  public WorkOrderResponse get(@PathVariable Long id) {
    return workOrderService.get(id);
  }

  @PostMapping("/{id}" + WorkOrderRoutes.STATUS)
  @RequireRole({UserRole.QUALITY_MANAGER, UserRole.LINE_SUPERVISOR})
  public WorkOrderResponse changeStatus(@PathVariable Long id, @RequestParam String status) {
    return workOrderService.changeStatus(id, status);
  }

  @GetMapping("/routes")
  public Map<String, String> routes() {
    return Map.of(
        "create", "POST /api/work-orders",
        "list", "GET /api/work-orders",
        "get", "GET /api/work-orders/{id}",
        "status", "POST /api/work-orders/{id}/status"
    );
  }
}

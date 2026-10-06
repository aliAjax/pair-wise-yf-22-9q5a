package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.routes.TraceRoutes;
import com.generated.qualityTrace.services.TraceService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** 全链路追溯：工单 -> 批次 -> 检验 -> 不良 -> 放行结论。 */
@RestController
public class TraceController {
  private final TraceService traceService;

  public TraceController(TraceService traceService) {
    this.traceService = traceService;
  }

  @GetMapping(TraceRoutes.PATH)
  public Map<String, Object> trace(@PathVariable String batchNo) {
    return traceService.traceTree(batchNo);
  }
}

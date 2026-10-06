package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constructors.TraceResponse;
import com.generated.qualityTrace.routes.TraceRoutes;
import com.generated.qualityTrace.services.TraceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 追溯控制器：按批次号查询全链路追溯树。
 */
@RestController
@RequestMapping(TraceRoutes.BASE)
public class TraceController {

  private final TraceService traceService;

  public TraceController(TraceService traceService) {
    this.traceService = traceService;
  }

  @GetMapping("/{batchNo}")
  public TraceResponse trace(@PathVariable String batchNo) {
    return traceService.traceByBatchNo(batchNo);
  }
}

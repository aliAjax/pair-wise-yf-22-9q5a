package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.constructors.ProductBatchResponse;
import com.generated.qualityTrace.constructors.ReleaseConclusionDtoFactory;
import com.generated.qualityTrace.constructors.ReleaseConclusionResponse;
import com.generated.qualityTrace.middlewares.RequireRole;
import com.generated.qualityTrace.models.ReleaseConclusion;
import com.generated.qualityTrace.routes.ProductBatchRoutes;
import com.generated.qualityTrace.services.ProductBatchService;
import com.generated.qualityTrace.services.ReleaseChainService;
import com.generated.qualityTrace.types.ProductBatchPayload;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 批次控制器。
 */
@RestController
@RequestMapping(ProductBatchRoutes.BASE)
public class ProductBatchController {

  private final ProductBatchService batchService;
  private final ReleaseChainService releaseChainService;

  public ProductBatchController(ProductBatchService batchService, ReleaseChainService releaseChainService) {
    this.batchService = batchService;
    this.releaseChainService = releaseChainService;
  }

  @PostMapping
  @RequireRole({UserRole.QUALITY_MANAGER, UserRole.LINE_SUPERVISOR})
  public ProductBatchResponse create(@Valid @RequestBody ProductBatchPayload payload) {
    return batchService.create(payload);
  }

  @GetMapping
  public List<ProductBatchResponse> list() {
    return batchService.list();
  }

  @GetMapping("/{id}")
  public ProductBatchResponse get(@PathVariable Long id) {
    return batchService.get(id);
  }

  /**
   * 查询批次当前放行结论（缺失或已作废时自动重算）。
   */
  @GetMapping(ProductBatchRoutes.RELEASE_CONCLUSION)
  public ReleaseConclusionResponse releaseConclusion(@PathVariable Long id) {
    batchService.getEntity(id);
    ReleaseConclusion conclusion = releaseChainService.getOrRecompute(id);
    return ReleaseConclusionDtoFactory.from(conclusion);
  }
}

package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.constructors.ReleaseConclusionDtoFactory;
import com.generated.qualityTrace.constructors.ReleaseConclusionResponse;
import com.generated.qualityTrace.middlewares.RequireRole;
import com.generated.qualityTrace.models.ReleaseConclusion;
import com.generated.qualityTrace.routes.ProductBatchRoutes;
import com.generated.qualityTrace.services.ProductBatchService;
import com.generated.qualityTrace.services.ReleaseChainService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 放行控制器。
 *
 * <p>只有质量经理能执行放行 / 解除待处置；质检员、产线主管、审计员等越权调用
 * 会被 RBAC 中间件直接 403 拒绝。</p>
 */
@RestController
@RequestMapping(ProductBatchRoutes.BASE)
public class ReleaseController {

  private final ReleaseChainService releaseChainService;
  private final ProductBatchService batchService;

  public ReleaseController(ReleaseChainService releaseChainService, ProductBatchService batchService) {
    this.releaseChainService = releaseChainService;
    this.batchService = batchService;
  }

  /**
   * 放行批次 / 解除待处置。
   */
  @PostMapping(ProductBatchRoutes.RELEASE)
  @RequireRole(UserRole.QUALITY_MANAGER)
  public ReleaseConclusionResponse release(@PathVariable Long id) {
    batchService.getEntity(id);
    ReleaseConclusion conclusion = releaseChainService.release(id);
    return ReleaseConclusionDtoFactory.from(conclusion);
  }
}

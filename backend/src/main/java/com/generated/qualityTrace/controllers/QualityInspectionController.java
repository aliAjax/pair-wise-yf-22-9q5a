package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.constructors.QualityInspectionResponse;
import com.generated.qualityTrace.middlewares.RequireRole;
import com.generated.qualityTrace.routes.QualityInspectionRoutes;
import com.generated.qualityTrace.services.QualityInspectionService;
import com.generated.qualityTrace.types.QualityInspectionPayload;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 质量检验控制器。
 */
@RestController
@RequestMapping(QualityInspectionRoutes.BASE)
public class QualityInspectionController {

  private final QualityInspectionService inspectionService;

  public QualityInspectionController(QualityInspectionService inspectionService) {
    this.inspectionService = inspectionService;
  }

  @PostMapping
  @RequireRole({UserRole.INSPECTOR, UserRole.LINE_SUPERVISOR})
  public QualityInspectionResponse submit(@Valid @RequestBody QualityInspectionPayload payload) {
    return inspectionService.submit(payload);
  }

  @GetMapping
  public List<QualityInspectionResponse> list() {
    return inspectionService.list();
  }

  @GetMapping("/{id}")
  public QualityInspectionResponse get(@PathVariable Long id) {
    return inspectionService.get(id);
  }
}

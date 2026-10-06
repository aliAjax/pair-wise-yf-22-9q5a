package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.constructors.DefectRecordResponse;
import com.generated.qualityTrace.middlewares.RequireRole;
import com.generated.qualityTrace.routes.DefectRecordRoutes;
import com.generated.qualityTrace.services.DefectRecordService;
import com.generated.qualityTrace.types.DefectRecordPayload;
import com.generated.qualityTrace.types.DispositionPayload;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 不良记录控制器。
 */
@RestController
@RequestMapping(DefectRecordRoutes.BASE)
public class DefectRecordController {

  private final DefectRecordService defectService;

  public DefectRecordController(DefectRecordService defectService) {
    this.defectService = defectService;
  }

  @PostMapping
  @RequireRole({UserRole.INSPECTOR, UserRole.QUALITY_MANAGER})
  public DefectRecordResponse register(@Valid @RequestBody DefectRecordPayload payload) {
    return defectService.register(payload);
  }

  @GetMapping
  public List<DefectRecordResponse> list() {
    return defectService.list();
  }

  @GetMapping("/{id}")
  public DefectRecordResponse get(@PathVariable Long id) {
    return defectService.get(id);
  }

  /**
   * 处置 / 关闭不良。处置状态变化会触发放行结论作废重算。
   */
  @PostMapping(DefectRecordRoutes.DISPOSITION)
  @RequireRole({UserRole.INSPECTOR, UserRole.QUALITY_MANAGER})
  public DefectRecordResponse disposition(@PathVariable Long id,
                                          @Valid @RequestBody DispositionPayload payload) {
    return defectService.updateDisposition(id, payload);
  }
}

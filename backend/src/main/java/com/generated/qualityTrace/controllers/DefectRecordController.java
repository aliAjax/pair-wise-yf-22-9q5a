package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.RoleCode;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.routes.DefectRecordRoutes;
import com.generated.qualityTrace.services.DefectRecordService;
import com.generated.qualityTrace.types.DefectDispositionPayload;
import com.generated.qualityTrace.types.DefectRecordPayload;
import com.generated.qualityTrace.validators.DefectRecordValidator;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 不良记录：登记与处置都会让对应批次的放行结论作废重算。 */
@RestController
@RequestMapping(DefectRecordRoutes.PATH)
public class DefectRecordController {
  private final DefectRecordService service;

  public DefectRecordController(DefectRecordService service) {
    this.service = service;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list().stream()
        .map(DefectRecordDtoFactory::toResponse)
        .collect(Collectors.toList());
  }

  /** 登记不良：严重不良会把已放行批次拉回待处置。 */
  @PostMapping
  public Map<String, Object> register(@RequestBody DefectRecordPayload payload,
                                      HttpServletRequest request) {
    RbacMiddleware.requireRole(request,
        RoleCode.INSPECTOR, RoleCode.LINE_SUPERVISOR, RoleCode.QUALITY_MANAGER);
    DefectRecordValidator.validateCreate(payload);
    return DefectRecordDtoFactory.toResponse(service.register(payload));
  }

  /** 处置不良：处置后放行结论立即作废重算。 */
  @PostMapping(DefectRecordRoutes.DISPOSITION)
  public Map<String, Object> dispose(@PathVariable Long id,
                                     @RequestBody DefectDispositionPayload payload,
                                     HttpServletRequest request) {
    RbacMiddleware.requireRole(request,
        RoleCode.INSPECTOR, RoleCode.LINE_SUPERVISOR, RoleCode.QUALITY_MANAGER);
    DefectRecordValidator.validateDisposition(payload);
    return DefectRecordDtoFactory.toResponse(service.dispose(id, payload));
  }
}

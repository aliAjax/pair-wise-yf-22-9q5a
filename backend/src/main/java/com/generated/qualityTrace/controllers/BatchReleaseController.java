package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.RoleCode;
import com.generated.qualityTrace.constructors.BatchReleaseDtoFactory;
import com.generated.qualityTrace.constructors.ReleaseSubmissionDtoFactory;
import com.generated.qualityTrace.exceptions.InvalidPayloadException;
import com.generated.qualityTrace.middlewares.AuthMiddleware;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.models.ReleaseSubmission;
import com.generated.qualityTrace.routes.BatchReleaseRoutes;
import com.generated.qualityTrace.services.ReleaseChainService;
import com.generated.qualityTrace.types.ReleaseSubmissionPayload;
import com.generated.qualityTrace.validators.ReleaseSubmissionValidator;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 放行链接口：查询结论、提交放行、解除待处置。 */
@RestController
public class BatchReleaseController {
  private final ReleaseChainService releaseChainService;

  public BatchReleaseController(ReleaseChainService releaseChainService) {
    this.releaseChainService = releaseChainService;
  }

  /** 查询批次当前放行结论（含提交记录）。 */
  @GetMapping(BatchReleaseRoutes.RELEASE)
  public Map<String, Object> getRelease(@PathVariable String batchNo) {
    BatchRelease release = releaseChainService.getRelease(batchNo);
    Map<String, Object> body = new LinkedHashMap<>(
        BatchReleaseDtoFactory.toResponse(release, releaseChainService.countOpenCritical(batchNo)));
    body.put("submissions", releaseChainService.listSubmissions(batchNo).stream()
        .map(ReleaseSubmissionDtoFactory::toResponse).collect(Collectors.toList()));
    return body;
  }

  /** 提交放行：质检员/产线主管/质量经理可提交；同一周期先到生效，晚到留待复核。 */
  @PostMapping(BatchReleaseRoutes.SUBMISSIONS)
  public Map<String, Object> submit(@PathVariable String batchNo,
                                    @RequestBody(required = false) ReleaseSubmissionPayload payload,
                                    HttpServletRequest request) {
    String role = RbacMiddleware.requireRole(request,
        RoleCode.INSPECTOR, RoleCode.LINE_SUPERVISOR, RoleCode.QUALITY_MANAGER);
    ReleaseSubmissionValidator.validate(batchNo, payload);
    String actorId = payload != null && payload.actorId() != null && !payload.actorId().isBlank()
        ? payload.actorId()
        : (String) request.getAttribute(AuthMiddleware.ATTR_ACTOR);
    if (actorId == null || actorId.isBlank()) {
      throw new InvalidPayloadException("actorId 不能为空");
    }
    String note = payload == null ? null : payload.note();
    ReleaseSubmission submission = releaseChainService.submit(batchNo, actorId, role, note);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("submission", ReleaseSubmissionDtoFactory.toResponse(submission));
    body.put("release", BatchReleaseDtoFactory.toResponse(
        releaseChainService.getRelease(batchNo), releaseChainService.countOpenCritical(batchNo)));
    return body;
  }

  /** 解除待处置：只有质量经理能调，其他岗位越权提交直接拒绝。 */
  @PostMapping(BatchReleaseRoutes.RESOLVE)
  public Map<String, Object> resolve(@PathVariable String batchNo, HttpServletRequest request) {
    String role = RbacMiddleware.requireAuthenticated(request);
    Object actor = request.getAttribute(AuthMiddleware.ATTR_ACTOR);
    String actorId = actor == null ? "unknown" : actor.toString();
    BatchRelease release = releaseChainService.resolveDisposition(batchNo, actorId, role);
    return BatchReleaseDtoFactory.toResponse(release, releaseChainService.countOpenCritical(batchNo));
  }
}

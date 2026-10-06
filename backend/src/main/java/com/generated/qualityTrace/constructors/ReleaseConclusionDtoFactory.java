package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.ReleaseConclusionStatus;
import com.generated.qualityTrace.models.ReleaseConclusion;
import com.generated.qualityTrace.utils.Formatters;

/**
 * 放行结论 DTO 构造器。
 */
public final class ReleaseConclusionDtoFactory {

  private ReleaseConclusionDtoFactory() {
  }

  public static ReleaseConclusionResponse from(ReleaseConclusion entity) {
    if (entity == null) {
      return null;
    }
    boolean releasable = ReleaseConclusionStatus.RELEASED.name().equals(entity.getConclusionStatus());
    return new ReleaseConclusionResponse(
        entity.getId(),
        entity.getBatchId(),
        entity.getConclusionStatus(),
        Formatters.conclusionLabel(entity.getConclusionStatus()),
        entity.getValid(),
        entity.getEffectiveFinalInspectionId(),
        entity.getBlockingDefectCount(),
        Formatters.formatDateTime(entity.getComputedAt()),
        Formatters.formatDateTime(entity.getReleasedAt()),
        entity.getReleasedBy(),
        releasable
    );
  }

  /**
   * 默认结论对象：尚未终检。
   */
  public static ReleaseConclusion empty(Long batchId) {
    ReleaseConclusion conclusion = new ReleaseConclusion();
    conclusion.setBatchId(batchId);
    conclusion.setConclusionStatus(ReleaseConclusionStatus.PENDING_FINAL_INSPECTION.name());
    conclusion.setValid(true);
    conclusion.setBlockingDefectCount(0);
    return conclusion;
  }
}

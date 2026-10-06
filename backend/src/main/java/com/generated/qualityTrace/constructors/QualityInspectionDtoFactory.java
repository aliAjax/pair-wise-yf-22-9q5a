package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.QualityInspection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QualityInspectionDtoFactory {
  private QualityInspectionDtoFactory() {}

  public static Map<String, Object> toResponse(QualityInspection i) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", i.id);
    m.put("batchId", i.batchId);
    m.put("inspectorId", i.inspectorId);
    m.put("inspectionType", i.inspectionType);
    m.put("standardVersion", i.standardVersion);
    m.put("resultStatus", i.resultStatus);
    m.put("inspectedAt", i.inspectedAt);
    return m;
  }
}

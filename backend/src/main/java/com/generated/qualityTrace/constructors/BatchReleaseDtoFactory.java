package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.utils.Formatters;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BatchReleaseDtoFactory {
  private BatchReleaseDtoFactory() {}

  public static Map<String, Object> toResponse(BatchRelease r, long openCriticalDefects) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("batchNo", r.batchNo);
    m.put("workOrderId", r.workOrderId);
    m.put("status", r.status);
    m.put("statusText", Formatters.releaseStatusText(r.status));
    m.put("reason", r.reason);
    m.put("cycle", r.cycle);
    m.put("stale", r.stale);
    m.put("computedAt", r.computedAt);
    m.put("openCriticalDefects", openCriticalDefects);
    return m;
  }
}

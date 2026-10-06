package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.ReleaseSubmission;
import com.generated.qualityTrace.utils.Formatters;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReleaseSubmissionDtoFactory {
  private ReleaseSubmissionDtoFactory() {}

  public static Map<String, Object> toResponse(ReleaseSubmission s) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", s.id);
    m.put("batchNo", s.batchNo);
    m.put("cycle", s.cycle);
    m.put("actorId", s.actorId);
    m.put("role", s.role);
    m.put("outcome", s.outcome);
    m.put("outcomeText", Formatters.submissionOutcomeText(s.outcome));
    m.put("note", s.note);
    m.put("submittedAt", s.submittedAt);
    return m;
  }
}

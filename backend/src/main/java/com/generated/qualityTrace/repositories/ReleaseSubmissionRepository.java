package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.ReleaseSubmission;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ReleaseSubmissionRepository {
  /** key = batchNo#cycle，putIfAbsent 原子占位，保证并发下先到先生效。 */
  private final ConcurrentHashMap<String, Boolean> appliedKeys = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Long, ReleaseSubmission> byId = new ConcurrentHashMap<>();
  private final AtomicLong seq = new AtomicLong(0);

  /** 原子占位：本周期第一个提交返回 true（生效），其余返回 false（待复核）。 */
  public boolean tryReserveApplied(String batchNo, int cycle) {
    return appliedKeys.putIfAbsent(batchNo + "#" + cycle, Boolean.TRUE) == null;
  }

  public ReleaseSubmission save(ReleaseSubmission submission) {
    if (submission.id == null) {
      submission.id = seq.incrementAndGet();
    }
    byId.put(submission.id, submission);
    return submission;
  }

  public List<ReleaseSubmission> findByBatchNo(String batchNo) {
    return byId.values().stream()
        .filter(s -> s.batchNo.equals(batchNo))
        .collect(Collectors.toList());
  }
}

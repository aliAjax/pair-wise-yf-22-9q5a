package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.BatchRelease;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class BatchReleaseRepository {
  private final ConcurrentHashMap<String, BatchRelease> byBatchNo = new ConcurrentHashMap<>();
  private final AtomicLong seq = new AtomicLong(0);

  public Optional<BatchRelease> findByBatchNo(String batchNo) {
    return Optional.ofNullable(byBatchNo.get(batchNo));
  }

  /** 按批次号 upsert：一个批次只有一条当前放行结论。 */
  public BatchRelease save(BatchRelease release) {
    if (release.id == null) {
      release.id = seq.incrementAndGet();
    }
    byBatchNo.put(release.batchNo, release);
    return release;
  }
}

package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.models.DefectRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class DefectRecordRepository {
  private final ConcurrentHashMap<Long, DefectRecord> byId = new ConcurrentHashMap<>();
  private final AtomicLong seq = new AtomicLong(0);

  public Optional<DefectRecord> findById(Long id) {
    return Optional.ofNullable(byId.get(id));
  }

  public List<DefectRecord> findAll() {
    return new ArrayList<>(byId.values());
  }

  public List<DefectRecord> findByBatchId(Long batchId) {
    return byId.values().stream()
        .filter(d -> d.batchId.equals(batchId))
        .collect(Collectors.toList());
  }

  /** 未处置的严重不良数量：> 0 时批次必须停在待处置。 */
  public long countOpenCriticalByBatchId(Long batchId) {
    return byId.values().stream()
        .filter(d -> d.batchId.equals(batchId))
        .filter(d -> DefectSeverity.CRITICAL.name().equals(d.severity))
        .filter(d -> DefectDispositionStatus.OPEN.name().equals(d.dispositionStatus))
        .count();
  }

  public DefectRecord save(DefectRecord defect) {
    if (defect.id == null) {
      defect.id = seq.incrementAndGet();
    }
    byId.put(defect.id, defect);
    return defect;
  }
}

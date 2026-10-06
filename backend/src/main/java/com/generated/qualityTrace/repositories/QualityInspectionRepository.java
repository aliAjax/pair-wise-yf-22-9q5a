package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.models.QualityInspection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class QualityInspectionRepository {
  private final ConcurrentHashMap<Long, QualityInspection> byId = new ConcurrentHashMap<>();
  private final AtomicLong seq = new AtomicLong(0);

  public QualityInspectionRepository() {
    save(new QualityInspection(null, 1L, "insp-01", InspectionType.FINAL.name(), "STD-V3",
        InspectionResultStatus.PASS.name(), "2026-10-06T11:00:00Z"));
  }

  public List<QualityInspection> findAll() {
    return new ArrayList<>(byId.values());
  }

  public List<QualityInspection> findByBatchId(Long batchId) {
    return byId.values().stream()
        .filter(i -> i.batchId.equals(batchId))
        .collect(Collectors.toList());
  }

  /** 放行链只认最近一次终检结论。 */
  public Optional<QualityInspection> findLatestFinalByBatchId(Long batchId) {
    return byId.values().stream()
        .filter(i -> i.batchId.equals(batchId) && InspectionType.FINAL.name().equals(i.inspectionType))
        .max(Comparator.comparing(i -> i.inspectedAt));
  }

  public QualityInspection save(QualityInspection inspection) {
    if (inspection.id == null) {
      inspection.id = seq.incrementAndGet();
    }
    byId.put(inspection.id, inspection);
    return inspection;
  }
}

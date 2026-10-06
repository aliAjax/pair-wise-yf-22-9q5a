package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.models.ProductBatch;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class ProductBatchRepository {
  private final ConcurrentHashMap<Long, ProductBatch> byId = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Long> idByBatchNo = new ConcurrentHashMap<>();
  private final AtomicLong seq = new AtomicLong(0);

  public ProductBatchRepository() {
    save(new ProductBatch(null, "BATCH-20261006-001", 1L, 200L, "MAT-LOT-7788",
        "2026-10-06T10:00:00Z", BatchStatus.COMPLETED.name()));
  }

  public Optional<ProductBatch> findById(Long id) {
    return Optional.ofNullable(byId.get(id));
  }

  public Optional<ProductBatch> findByBatchNo(String batchNo) {
    Long id = idByBatchNo.get(batchNo);
    return id == null ? Optional.empty() : findById(id);
  }

  public List<ProductBatch> findAll() {
    return new ArrayList<>(byId.values());
  }

  public ProductBatch save(ProductBatch batch) {
    if (batch.id == null) {
      batch.id = seq.incrementAndGet();
    }
    byId.put(batch.id, batch);
    idByBatchNo.put(batch.batchNo, batch.id);
    return batch;
  }
}

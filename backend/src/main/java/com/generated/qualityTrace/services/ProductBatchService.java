package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductBatchService {
  private final ProductBatchRepository repo;

  public ProductBatchService(ProductBatchRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream()
        .map(ProductBatchDtoFactory::toResponse)
        .collect(Collectors.toList());
  }
}

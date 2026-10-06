package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.BatchReleaseDtoFactory;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.constructors.ReleaseSubmissionDtoFactory;
import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.exceptions.BatchNotFoundException;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** 追溯服务：把工单 -> 批次 -> 检验 -> 不良 -> 放行结论拼成全链路追溯树。 */
@Service
public class TraceService {
  private final ProductBatchRepository batchRepo;
  private final WorkOrderRepository workOrderRepo;
  private final QualityInspectionRepository inspectionRepo;
  private final DefectRecordRepository defectRepo;
  private final ReleaseChainService releaseChainService;

  public TraceService(ProductBatchRepository batchRepo,
                      WorkOrderRepository workOrderRepo,
                      QualityInspectionRepository inspectionRepo,
                      DefectRecordRepository defectRepo,
                      ReleaseChainService releaseChainService) {
    this.batchRepo = batchRepo;
    this.workOrderRepo = workOrderRepo;
    this.inspectionRepo = inspectionRepo;
    this.defectRepo = defectRepo;
    this.releaseChainService = releaseChainService;
  }

  public Map<String, Object> traceTree(String batchNo) {
    ProductBatch batch = batchRepo.findByBatchNo(batchNo)
        .orElseThrow(() -> new BatchNotFoundException(batchNo));
    BatchRelease release = releaseChainService.getRelease(batchNo);

    Map<String, Object> tree = new LinkedHashMap<>();
    tree.put("batch", ProductBatchDtoFactory.toResponse(batch));
    tree.put("workOrder", workOrderRepo.findById(batch.workOrderId)
        .map(WorkOrderDtoFactory::toResponse).orElse(null));
    tree.put("inspections", inspectionRepo.findByBatchId(batch.id).stream()
        .map(QualityInspectionDtoFactory::toResponse).collect(Collectors.toList()));
    tree.put("defects", defectRepo.findByBatchId(batch.id).stream()
        .map(DefectRecordDtoFactory::toResponse).collect(Collectors.toList()));
    tree.put("openCriticalDefects", defectRepo.countOpenCriticalByBatchId(batch.id));
    tree.put("release", BatchReleaseDtoFactory.toResponse(
        release, releaseChainService.countOpenCritical(batchNo)));
    tree.put("submissions", releaseChainService.listSubmissions(batchNo).stream()
        .map(ReleaseSubmissionDtoFactory::toResponse).collect(Collectors.toList()));
    return tree;
  }
}

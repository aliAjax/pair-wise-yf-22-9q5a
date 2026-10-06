package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.exceptions.BatchNotFoundException;
import com.generated.qualityTrace.exceptions.BizException;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.types.DefectDispositionPayload;
import com.generated.qualityTrace.types.DefectRecordPayload;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefectRecordService {
  private static final Logger log = LoggerFactory.getLogger(DefectRecordService.class);

  private final DefectRecordRepository repo;
  private final ProductBatchRepository batchRepo;
  private final ReleaseChainService releaseChainService;

  public DefectRecordService(DefectRecordRepository repo,
                             ProductBatchRepository batchRepo,
                             ReleaseChainService releaseChainService) {
    this.repo = repo;
    this.batchRepo = batchRepo;
    this.releaseChainService = releaseChainService;
  }

  public List<DefectRecord> list() {
    return repo.findAll();
  }

  /** 登记不良：保存后立即让该批次的放行结论作废重算。 */
  public DefectRecord register(DefectRecordPayload payload) {
    ProductBatch batch = batchRepo.findById(payload.batchId())
        .orElseThrow(() -> new BatchNotFoundException("id=" + payload.batchId()));
    DefectRecord defect = new DefectRecord(
        null, payload.batchId(), payload.defectType(), payload.defectQty(),
        payload.severity(), payload.rootCause(),
        DefectDispositionStatus.OPEN.name(), Instant.now().toString());
    repo.save(defect);
    log.info(LogTemplates.DEFECT_REGISTERED, defect.id, defect.batchId, defect.severity, defect.defectQty);
    releaseChainService.onDefectChanged(batch.batchNo, "defect registered #" + defect.id);
    return defect;
  }

  /** 处置不良：处置状态变化同样触发放行结论作废重算。 */
  public DefectRecord dispose(Long id, DefectDispositionPayload payload) {
    DefectRecord defect = repo.findById(id)
        .orElseThrow(() -> new BizException("DEFECT_NOT_FOUND", "不良记录不存在: " + id, 404));
    defect.dispositionStatus = payload.dispositionStatus();
    if (payload.rootCause() != null && !payload.rootCause().isBlank()) {
      defect.rootCause = payload.rootCause();
    }
    repo.save(defect);
    log.info(LogTemplates.DEFECT_DISPOSED, defect.id, defect.batchId, defect.dispositionStatus);
    ProductBatch batch = batchRepo.findById(defect.batchId)
        .orElseThrow(() -> new BatchNotFoundException("id=" + defect.batchId));
    releaseChainService.onDefectChanged(batch.batchNo, "defect disposed #" + defect.id);
    return defect;
  }
}

package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.TraceDtoFactory;
import com.generated.qualityTrace.constructors.TraceResponse;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.ReleaseConclusion;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.InspectionItemResultMapper;
import com.generated.qualityTrace.utils.CurrentUser;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 追溯服务：按批次号拉通工单 → 批次 → 检验（含检验项）→ 不良 → 放行结论的全链路。
 */
@Service
public class TraceService {

  private static final Logger log = LoggerFactory.getLogger(TraceService.class);

  private final ProductBatchService batchService;
  private final WorkOrderService workOrderService;
  private final QualityInspectionService inspectionService;
  private final DefectRecordService defectService;
  private final ReleaseChainService releaseChainService;
  private final InspectionItemResultMapper itemMapper;
  private final AuditLogService auditLogService;

  public TraceService(ProductBatchService batchService,
                      WorkOrderService workOrderService,
                      QualityInspectionService inspectionService,
                      DefectRecordService defectService,
                      ReleaseChainService releaseChainService,
                      InspectionItemResultMapper itemMapper,
                      AuditLogService auditLogService) {
    this.batchService = batchService;
    this.workOrderService = workOrderService;
    this.inspectionService = inspectionService;
    this.defectService = defectService;
    this.releaseChainService = releaseChainService;
    this.itemMapper = itemMapper;
    this.auditLogService = auditLogService;
  }

  public TraceResponse traceByBatchNo(String batchNo) {
    ProductBatch batch = batchService.getEntityByBatchNo(batchNo);
    WorkOrder workOrder = workOrderService.getEntity(batch.getWorkOrderId());

    List<QualityInspection> inspections = inspectionService.listEntitiesByBatch(batch.getId());
    List<InspectionItemResult> allItems = new ArrayList<>();
    for (QualityInspection ins : inspections) {
      allItems.addAll(itemMapper.findByInspectionId(ins.getId()));
    }
    List<DefectRecord> defects = defectService.listEntitiesByBatch(batch.getId());
    ReleaseConclusion conclusion = releaseChainService.getOrRecompute(batch.getId());

    String operator = CurrentUser.get() == null ? "anonymous" : CurrentUser.get().username();
    log.info(LogTemplates.TRACE_QUERIED, batchNo, operator);
    log.info(LogTemplates.TRACE_CHAIN_BUILT, batchNo, workOrder.getId(),
        inspections.size(), defects.size(), conclusion.getConclusionStatus());
    auditLogService.record(operator, "TRACE_QUERY", "BATCH", String.valueOf(batch.getId()),
        "batchNo=" + batchNo);

    return TraceDtoFactory.build(batch, workOrder, inspections, allItems, defects, conclusion);
  }
}

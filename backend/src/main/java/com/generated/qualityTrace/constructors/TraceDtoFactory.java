package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.ReleaseConclusion;
import com.generated.qualityTrace.models.WorkOrder;
import java.util.Collections;
import java.util.List;

/**
 * 追溯链 DTO 构造器：把工单、批次、检验、不良与放行结论拼成一条链。
 */
public final class TraceDtoFactory {

  private TraceDtoFactory() {
  }

  public static TraceResponse build(ProductBatch batch,
                                    WorkOrder workOrder,
                                    List<QualityInspection> inspections,
                                    List<InspectionItemResult> allItems,
                                    List<DefectRecord> defects,
                                    ReleaseConclusion conclusion) {
    List<QualityInspectionResponse> inspectionResponses = inspections == null
        ? Collections.emptyList()
        : inspections.stream()
            .map(ins -> QualityInspectionDtoFactory.from(
                ins,
                allItems == null ? Collections.emptyList()
                    : allItems.stream().filter(it -> it.getInspectionId().equals(ins.getId())).toList()))
            .toList();

    List<DefectRecordResponse> defectResponses = defects == null
        ? Collections.emptyList()
        : defects.stream().map(DefectRecordDtoFactory::from).toList();

    String chainStatus = conclusion == null ? "PENDING_FINAL_INSPECTION" : conclusion.getConclusionStatus();

    return new TraceResponse(
        ProductBatchDtoFactory.from(batch),
        WorkOrderDtoFactory.from(workOrder),
        inspectionResponses,
        defectResponses,
        ReleaseConclusionDtoFactory.from(conclusion),
        chainStatus
    );
  }
}

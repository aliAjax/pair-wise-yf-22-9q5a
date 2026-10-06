package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.InspectionItemResultDtoFactory;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.constructors.QualityInspectionResponse;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.repositories.InspectionItemResultMapper;
import com.generated.qualityTrace.repositories.QualityInspectionMapper;
import com.generated.qualityTrace.types.QualityInspectionPayload;
import com.generated.qualityTrace.utils.CurrentUser;
import com.generated.qualityTrace.utils.DateUtils;
import com.generated.qualityTrace.validators.QualityInspectionValidator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 质量检验服务。
 *
 * <p>质检员与产线主管同时提交同一批次时，依赖数据库局部唯一索引先到先得：
 * 先到的置为 EFFECTIVE（生效），晚到的置为 PENDING_REVIEW（待复核），不覆盖已有结论。</p>
 */
@Service
public class QualityInspectionService {

  private static final Logger log = LoggerFactory.getLogger(QualityInspectionService.class);

  private final QualityInspectionMapper inspectionMapper;
  private final InspectionItemResultMapper itemMapper;
  private final ProductBatchService batchService;
  private final QualityInspectionValidator validator;
  private final ReleaseChainService releaseChainService;
  private final AuditLogService auditLogService;

  public QualityInspectionService(QualityInspectionMapper inspectionMapper,
                                  InspectionItemResultMapper itemMapper,
                                  ProductBatchService batchService,
                                  QualityInspectionValidator validator,
                                  ReleaseChainService releaseChainService,
                                  AuditLogService auditLogService) {
    this.inspectionMapper = inspectionMapper;
    this.itemMapper = itemMapper;
    this.batchService = batchService;
    this.validator = validator;
    this.releaseChainService = releaseChainService;
    this.auditLogService = auditLogService;
  }

  @Transactional
  public QualityInspectionResponse submit(QualityInspectionPayload payload) {
    validator.validateSubmit(payload);
    ProductBatch batch = batchService.getEntity(payload.batchId());
    CurrentUser.LoginUser user = CurrentUser.require();

    QualityInspection entity = QualityInspectionDtoFactory.empty();
    entity.setBatchId(batch.getId());
    entity.setInspectorId(user.id());
    entity.setInspectionType(payload.inspectionType().trim().toUpperCase());
    entity.setStandardVersion(payload.standardVersion());
    entity.setResultStatus(payload.resultStatus().trim().toUpperCase());
    entity.setInspectedAt(DateUtils.now());

    // 先尝试以「生效」插入；若同批次同类型已有生效单，则转落「待复核」
    int inserted = inspectionMapper.insertEffective(entity);
    boolean effective = inserted == 1;
    if (!effective) {
      entity.setReviewStatus("PENDING_REVIEW");
      inspectionMapper.insert(entity);
    }

    List<InspectionItemResult> items = saveItems(entity.getId(), payload);

    log.info(LogTemplates.INSPECTION_SUBMITTED, entity.getId(), batch.getId(),
        entity.getInspectionType(), entity.getResultStatus());
    if (effective) {
      log.info(LogTemplates.INSPECTION_EFFECTIVE, entity.getId(), batch.getId(), entity.getInspectionType());
      auditLogService.record(user.username(), "INSPECTION_SUBMIT", "INSPECTION",
          String.valueOf(entity.getId()), "effective type=" + entity.getInspectionType());
      // 生效检验（尤其终检）会改变放行链，触发重算
      releaseChainService.onInspectionChanged(batch.getId(), "inspection effective: " + entity.getInspectionType());
    } else {
      log.info(LogTemplates.INSPECTION_PENDING_REVIEW, entity.getId(), batch.getId(), entity.getInspectionType());
      auditLogService.record(user.username(), "INSPECTION_PENDING_REVIEW", "INSPECTION",
          String.valueOf(entity.getId()), "concurrent first-write-wins, pending review");
    }

    return QualityInspectionDtoFactory.from(entity, items);
  }

  private List<InspectionItemResult> saveItems(Long inspectionId, QualityInspectionPayload payload) {
    List<InspectionItemResult> items = new ArrayList<>();
    payload.items().forEach(itemPayload -> {
      InspectionItemResult item = new InspectionItemResult();
      item.setInspectionId(inspectionId);
      item.setItemCode(itemPayload.itemCode());
      item.setItemName(itemPayload.itemName());
      item.setMeasuredValue(itemPayload.measuredValue());
      item.setLimitMin(itemPayload.limitMin());
      item.setLimitMax(itemPayload.limitMax());
      item.setItemStatus(InspectionItemResultDtoFactory.judge(
          itemPayload.measuredValue(), itemPayload.limitMin(), itemPayload.limitMax()));
      itemMapper.insert(item);
      items.add(item);
    });
    log.info(LogTemplates.INSPECTION_ITEMS_RECORDED, inspectionId, items.size());
    return items;
  }

  public List<QualityInspectionResponse> list() {
    return inspectionMapper.selectList(new QueryWrapper<QualityInspection>().orderByDesc("id")).stream()
        .map(ins -> QualityInspectionDtoFactory.from(ins, itemMapper.findByInspectionId(ins.getId())))
        .toList();
  }

  public QualityInspection getEntity(Long id) {
    QualityInspection entity = inspectionMapper.selectById(id);
    if (entity == null) {
      throw BusinessException.of(404, ErrorCodes.INSPECTION_NOT_FOUND,
          String.format(ErrorMessages.INSPECTION_NOT_FOUND, id));
    }
    return entity;
  }

  public QualityInspectionResponse get(Long id) {
    QualityInspection entity = getEntity(id);
    return QualityInspectionDtoFactory.from(entity, itemMapper.findByInspectionId(id));
  }

  public List<QualityInspection> listEntitiesByBatch(Long batchId) {
    return inspectionMapper.selectList(
        new QueryWrapper<QualityInspection>().eq("batch_id", batchId).orderByAsc("id"));
  }
}

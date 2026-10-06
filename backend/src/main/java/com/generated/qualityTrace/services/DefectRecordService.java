package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.constructors.DefectRecordResponse;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.DefectRecordMapper;
import com.generated.qualityTrace.types.DefectRecordPayload;
import com.generated.qualityTrace.types.DispositionPayload;
import com.generated.qualityTrace.utils.CurrentUser;
import com.generated.qualityTrace.utils.DateUtils;
import com.generated.qualityTrace.validators.DefectRecordValidator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 不良记录服务。
 *
 * <p>不良记录一旦登记或处置变更，放行结论立即作废并重算：
 * 存在未处置严重不良时批次停在待处置。</p>
 */
@Service
public class DefectRecordService {

  private static final Logger log = LoggerFactory.getLogger(DefectRecordService.class);

  private final DefectRecordMapper defectMapper;
  private final ProductBatchService batchService;
  private final DefectRecordValidator validator;
  private final ReleaseChainService releaseChainService;
  private final AuditLogService auditLogService;

  public DefectRecordService(DefectRecordMapper defectMapper,
                             ProductBatchService batchService,
                             DefectRecordValidator validator,
                             ReleaseChainService releaseChainService,
                             AuditLogService auditLogService) {
    this.defectMapper = defectMapper;
    this.batchService = batchService;
    this.validator = validator;
    this.releaseChainService = releaseChainService;
    this.auditLogService = auditLogService;
  }

  @Transactional
  public DefectRecordResponse register(DefectRecordPayload payload) {
    validator.validateRegister(payload);
    ProductBatch batch = batchService.getEntity(payload.batchId());
    CurrentUser.LoginUser user = CurrentUser.require();

    DefectRecord entity = DefectRecordDtoFactory.empty();
    entity.setBatchId(batch.getId());
    entity.setDefectType(payload.defectType());
    entity.setDefectQty(payload.defectQty());
    entity.setSeverity(payload.severity().trim().toUpperCase());
    entity.setRootCause(payload.rootCause());
    if (payload.dispositionStatus() != null && !payload.dispositionStatus().isBlank()) {
      entity.setDispositionStatus(payload.dispositionStatus().trim().toUpperCase());
    }
    entity.setCreatedAt(DateUtils.now());
    entity.setUpdatedAt(DateUtils.now());
    defectMapper.insert(entity);

    DefectSeverity severity = DefectSeverity.from(entity.getSeverity());
    log.info(LogTemplates.DEFECT_REGISTERED, entity.getId(), batch.getId(), entity.getSeverity(),
        entity.getDefectQty());
    log.info(LogTemplates.DEFECT_SEVERITY_CLASSIFIED, entity.getId(), entity.getSeverity(),
        severity != null && severity.isBlocking());
    auditLogService.record(user.username(), "DEFECT_REGISTER", "DEFECT",
        String.valueOf(entity.getId()), "severity=" + entity.getSeverity());

    // 不良记录一变，放行结论作废重算
    releaseChainService.onDefectChanged(batch.getId(), "defect registered: " + entity.getSeverity());

    return DefectRecordDtoFactory.from(entity);
  }

  @Transactional
  public DefectRecordResponse updateDisposition(Long id, DispositionPayload payload) {
    DefectRecord entity = getEntity(id);
    DefectDispositionStatus target = validator.validateDisposition(payload);
    CurrentUser.LoginUser user = CurrentUser.require();

    String from = entity.getDispositionStatus();
    entity.setDispositionStatus(target.name());
    if (payload.rootCause() != null && !payload.rootCause().isBlank()) {
      entity.setRootCause(payload.rootCause());
    }
    entity.setUpdatedAt(DateUtils.now());
    defectMapper.updateById(entity);

    log.info(LogTemplates.DEFECT_UPDATED, entity.getId(), from, target.name());
    if (target == DefectDispositionStatus.CLOSED) {
      log.info(LogTemplates.DEFECT_CLOSED, entity.getId(), entity.getBatchId(), target.name());
    }
    auditLogService.record(user.username(), "DEFECT_DISPOSITION_CHANGE", "DEFECT",
        String.valueOf(entity.getId()), from + "->" + target.name());

    // 处置状态变化，放行结论作废重算
    releaseChainService.onDefectChanged(entity.getBatchId(),
        "defect disposition: " + from + "->" + target.name());

    return DefectRecordDtoFactory.from(entity);
  }

  public List<DefectRecordResponse> list() {
    return defectMapper.selectList(new QueryWrapper<DefectRecord>().orderByDesc("id")).stream()
        .map(DefectRecordDtoFactory::from)
        .toList();
  }

  public DefectRecord getEntity(Long id) {
    DefectRecord entity = defectMapper.selectById(id);
    if (entity == null) {
      throw BusinessException.of(404, ErrorCodes.DEFECT_NOT_FOUND,
          String.format(ErrorMessages.DEFECT_NOT_FOUND, id));
    }
    return entity;
  }

  public DefectRecordResponse get(Long id) {
    return DefectRecordDtoFactory.from(getEntity(id));
  }

  public List<DefectRecord> listEntitiesByBatch(Long batchId) {
    return defectMapper.selectList(
        new QueryWrapper<DefectRecord>().eq("batch_id", batchId).orderByAsc("id"));
  }
}

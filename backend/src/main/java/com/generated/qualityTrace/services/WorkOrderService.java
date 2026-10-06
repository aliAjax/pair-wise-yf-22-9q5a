package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.constructors.WorkOrderResponse;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.WorkOrderMapper;
import com.generated.qualityTrace.types.WorkOrderPayload;
import com.generated.qualityTrace.utils.CurrentUser;
import com.generated.qualityTrace.utils.DateUtils;
import com.generated.qualityTrace.validators.WorkOrderValidator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工单服务。
 */
@Service
public class WorkOrderService {

  private static final Logger log = LoggerFactory.getLogger(WorkOrderService.class);

  private final WorkOrderMapper workOrderMapper;
  private final WorkOrderValidator validator;
  private final AuditLogService auditLogService;

  public WorkOrderService(WorkOrderMapper workOrderMapper,
                          WorkOrderValidator validator,
                          AuditLogService auditLogService) {
    this.workOrderMapper = workOrderMapper;
    this.validator = validator;
    this.auditLogService = auditLogService;
  }

  @Transactional
  public WorkOrderResponse create(WorkOrderPayload payload) {
    validator.validateCreate(payload);
    WorkOrder entity = WorkOrderDtoFactory.empty();
    entity.setOrderNo(payload.orderNo());
    entity.setProductCode(payload.productCode());
    entity.setProductName(payload.productName());
    entity.setPlannedQty(payload.plannedQty());
    entity.setLineCode(payload.lineCode());
    entity.setStartAt(payload.startAt() == null ? DateUtils.now() : DateUtils.parse(payload.startAt()));
    if (payload.status() != null && !payload.status().isBlank()) {
      entity.setStatus(validator.validateStatus(payload.status()).name());
    }
    workOrderMapper.insert(entity);
    log.info(LogTemplates.WO_CREATED, entity.getId(), entity.getOrderNo(), entity.getProductCode());
    auditLogService.record(operator(), "WORK_ORDER_CREATE", "WORK_ORDER", String.valueOf(entity.getId()),
        "orderNo=" + entity.getOrderNo());
    return WorkOrderDtoFactory.from(entity);
  }

  public List<WorkOrderResponse> list() {
    return workOrderMapper.selectList(new QueryWrapper<WorkOrder>().orderByDesc("id")).stream()
        .map(WorkOrderDtoFactory::from)
        .toList();
  }

  public WorkOrder getEntity(Long id) {
    WorkOrder entity = workOrderMapper.selectById(id);
    if (entity == null) {
      throw BusinessException.of(404, ErrorCodes.WORK_ORDER_NOT_FOUND,
          String.format(ErrorMessages.WORK_ORDER_NOT_FOUND, id));
    }
    return entity;
  }

  public WorkOrderResponse get(Long id) {
    return WorkOrderDtoFactory.from(getEntity(id));
  }

  @Transactional
  public WorkOrderResponse changeStatus(Long id, String rawStatus) {
    WorkOrder entity = getEntity(id);
    String from = entity.getStatus();
    String to = validator.validateStatus(rawStatus).name();
    entity.setStatus(to);
    workOrderMapper.updateById(entity);
    log.info(LogTemplates.WO_STATUS_CHANGED, entity.getId(), from, to);
    auditLogService.record(operator(), "WORK_ORDER_STATUS_CHANGE", "WORK_ORDER", String.valueOf(id),
        from + "->" + to);
    return WorkOrderDtoFactory.from(entity);
  }

  private String operator() {
    CurrentUser.LoginUser user = CurrentUser.get();
    return user == null ? "system" : user.username();
  }
}

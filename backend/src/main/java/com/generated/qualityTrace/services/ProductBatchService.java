package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.generated.qualityTrace.common.BusinessException;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.constructors.ProductBatchResponse;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.ProductBatchMapper;
import com.generated.qualityTrace.types.ProductBatchPayload;
import com.generated.qualityTrace.utils.CurrentUser;
import com.generated.qualityTrace.utils.DateUtils;
import com.generated.qualityTrace.validators.ProductBatchValidator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 批次服务。
 */
@Service
public class ProductBatchService {

  private static final Logger log = LoggerFactory.getLogger(ProductBatchService.class);

  private final ProductBatchMapper batchMapper;
  private final WorkOrderService workOrderService;
  private final ProductBatchValidator validator;
  private final ReleaseChainService releaseChainService;
  private final AuditLogService auditLogService;

  public ProductBatchService(ProductBatchMapper batchMapper,
                             WorkOrderService workOrderService,
                             ProductBatchValidator validator,
                             ReleaseChainService releaseChainService,
                             AuditLogService auditLogService) {
    this.batchMapper = batchMapper;
    this.workOrderService = workOrderService;
    this.validator = validator;
    this.releaseChainService = releaseChainService;
    this.auditLogService = auditLogService;
  }

  @Transactional
  public ProductBatchResponse create(ProductBatchPayload payload) {
    validator.validateCreate(payload);
    WorkOrder workOrder = workOrderService.getEntity(payload.workOrderId());

    ProductBatch entity = ProductBatchDtoFactory.empty();
    entity.setBatchNo(payload.batchNo());
    entity.setWorkOrderId(workOrder.getId());
    entity.setQuantity(payload.quantity());
    entity.setMaterialLotNo(payload.materialLotNo());
    entity.setProducedAt(payload.producedAt() == null ? DateUtils.now() : DateUtils.parse(payload.producedAt()));
    batchMapper.insert(entity);

    // 建批次同时初始放行链结论（待终检）
    releaseChainService.recompute(entity.getId());

    log.info(LogTemplates.BATCH_CREATED, entity.getId(), entity.getBatchNo(), entity.getWorkOrderId());
    auditLogService.record(operator(), "BATCH_CREATE", "BATCH", String.valueOf(entity.getId()),
        "batchNo=" + entity.getBatchNo() + ", workOrderId=" + workOrder.getId());
    return ProductBatchDtoFactory.from(entity);
  }

  public List<ProductBatchResponse> list() {
    return batchMapper.selectList(new QueryWrapper<ProductBatch>().orderByDesc("id")).stream()
        .map(ProductBatchDtoFactory::from)
        .toList();
  }

  public ProductBatch getEntity(Long id) {
    ProductBatch entity = batchMapper.selectById(id);
    if (entity == null) {
      throw BusinessException.of(404, ErrorCodes.BATCH_NOT_FOUND,
          String.format(ErrorMessages.BATCH_NOT_FOUND, id));
    }
    return entity;
  }

  public ProductBatchResponse get(Long id) {
    return ProductBatchDtoFactory.from(getEntity(id));
  }

  public ProductBatch getEntityByBatchNo(String batchNo) {
    ProductBatch entity = batchMapper.findByBatchNo(batchNo);
    if (entity == null) {
      throw BusinessException.of(404, ErrorCodes.BATCH_NOT_FOUND,
          String.format(ErrorMessages.BATCH_NOT_FOUND, batchNo));
    }
    return entity;
  }

  public List<ProductBatchResponse> listByWorkOrder(Long workOrderId) {
    return batchMapper.selectList(
            new QueryWrapper<ProductBatch>().eq("work_order_id", workOrderId).orderByDesc("id"))
        .stream().map(ProductBatchDtoFactory::from).toList();
  }

  private String operator() {
    CurrentUser.LoginUser user = CurrentUser.get();
    return user == null ? "system" : user.username();
  }
}

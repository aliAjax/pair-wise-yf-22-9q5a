package com.generated.qualityTrace.services;

import com.generated.qualityTrace.models.AuditLog;
import com.generated.qualityTrace.repositories.AuditLogMapper;
import com.generated.qualityTrace.utils.DateUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 审计日志服务：写操作落 audit_log 表，同时输出日志模板。
 */
@Service
public class AuditLogService {

  private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

  private final AuditLogMapper auditLogMapper;

  public AuditLogService(AuditLogMapper auditLogMapper) {
    this.auditLogMapper = auditLogMapper;
  }

  public void record(String actor, String action, String targetType, String targetId, String detail) {
    AuditLog entry = new AuditLog();
    entry.setActor(actor);
    entry.setAction(action);
    entry.setTargetType(targetType);
    entry.setTargetId(targetId);
    entry.setDetail(detail);
    entry.setCreatedAt(DateUtils.now());
    auditLogMapper.insert(entry);
    log.info("audit.write actor={} action={} target={}/{}", actor, action, targetType, targetId);
  }

  public List<AuditLog> findByTarget(String targetType, String targetId) {
    return auditLogMapper.findByTarget(targetType, targetId);
  }
}

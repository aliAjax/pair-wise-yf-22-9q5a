package com.generated.qualityTrace.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.generated.qualityTrace.models.AuditLog;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 审计日志数据访问层。
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {

  @Select("SELECT * FROM audit_log WHERE target_type = #{targetType} AND target_id = #{targetId} ORDER BY id")
  List<AuditLog> findByTarget(@Param("targetType") String targetType, @Param("targetId") String targetId);
}

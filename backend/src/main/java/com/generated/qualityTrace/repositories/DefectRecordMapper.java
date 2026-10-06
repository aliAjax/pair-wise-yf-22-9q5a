package com.generated.qualityTrace.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.generated.qualityTrace.models.DefectRecord;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 不良记录数据访问层。
 */
@Mapper
public interface DefectRecordMapper extends BaseMapper<DefectRecord> {

  /**
   * 统计某批次未处置的严重不良（MAJOR/CRITICAL 且 disposition_status <> CLOSED）数量。
   * 这是放行链是否卡住的关键计数。
   */
  @Select("""
      SELECT COUNT(*) FROM defect_record
      WHERE batch_id = #{batchId}
        AND severity IN ('MAJOR', 'CRITICAL')
        AND disposition_status <> 'CLOSED'
      """)
  long countBlocking(@Param("batchId") Long batchId);

  /**
   * 列出某批次未处置的严重不良。
   */
  @Select("""
      SELECT * FROM defect_record
      WHERE batch_id = #{batchId}
        AND severity IN ('MAJOR', 'CRITICAL')
        AND disposition_status <> 'CLOSED'
      ORDER BY id
      """)
  List<DefectRecord> findBlocking(@Param("batchId") Long batchId);

  @Select("SELECT * FROM defect_record WHERE batch_id = #{batchId} ORDER BY id")
  List<DefectRecord> findByBatchId(@Param("batchId") Long batchId);
}

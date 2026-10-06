package com.generated.qualityTrace.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.generated.qualityTrace.models.ReleaseConclusion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 放行结论数据访问层。
 */
@Mapper
public interface ReleaseConclusionMapper extends BaseMapper<ReleaseConclusion> {

  @Select("SELECT * FROM release_conclusion WHERE batch_id = #{batchId}")
  ReleaseConclusion findByBatchId(@Param("batchId") Long batchId);

  /**
   * 不良记录变更时，先把结论标记为作废（valid=false），等待重算。
   */
  @Update("UPDATE release_conclusion SET valid = false WHERE batch_id = #{batchId}")
  int markStale(@Param("batchId") Long batchId);
}

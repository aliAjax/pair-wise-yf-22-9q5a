package com.generated.qualityTrace.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.generated.qualityTrace.models.QualityInspection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 质量检验数据访问层。
 */
@Mapper
public interface QualityInspectionMapper extends BaseMapper<QualityInspection> {

  /**
   * 以「生效」身份插入检验单。依赖数据库局部唯一索引
   * {@code uq_effective_inspection (batch_id, inspection_type) WHERE review_status='EFFECTIVE'}
   * 实现先到先得：已存在同批次同类型生效单时跳过（返回 0），由调用方改落「待复核」。
   *
   * @return 实际插入行数（1=生效插入成功，0=已存在生效单）
   */
  @Insert("""
      INSERT INTO quality_inspection
        (batch_id, inspector_id, inspection_type, standard_version, result_status, review_status, inspected_at)
      VALUES
        (#{batchId}, #{inspectorId}, #{inspectionType}, #{standardVersion}, #{resultStatus}, 'EFFECTIVE', #{inspectedAt})
      ON CONFLICT (batch_id, inspection_type) WHERE review_status = 'EFFECTIVE'
      DO NOTHING
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insertEffective(QualityInspection inspection);

  /**
   * 查找某批次指定检验类型的生效检验单（同类型仅一张生效）。
   */
  @Select("""
      SELECT * FROM quality_inspection
      WHERE batch_id = #{batchId}
        AND inspection_type = #{inspectionType}
        AND review_status = 'EFFECTIVE'
      ORDER BY id DESC
      LIMIT 1
      """)
  QualityInspection findEffective(@Param("batchId") Long batchId,
                                  @Param("inspectionType") String inspectionType);

  /**
   * 查找某批次的生效终检单。
   */
  default QualityInspection findEffectiveFinal(Long batchId) {
    return findEffective(batchId, "FINAL");
  }
}

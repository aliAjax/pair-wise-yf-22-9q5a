package com.generated.qualityTrace.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.generated.qualityTrace.models.InspectionItemResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 检验项结果数据访问层。
 */
@Mapper
public interface InspectionItemResultMapper extends BaseMapper<InspectionItemResult> {

  @Select("SELECT * FROM inspection_item_result WHERE inspection_id = #{inspectionId} ORDER BY id")
  List<InspectionItemResult> findByInspectionId(@Param("inspectionId") Long inspectionId);
}

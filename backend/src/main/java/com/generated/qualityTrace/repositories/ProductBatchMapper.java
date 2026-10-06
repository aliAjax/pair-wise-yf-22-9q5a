package com.generated.qualityTrace.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.generated.qualityTrace.models.ProductBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 批次数据访问层。
 */
@Mapper
public interface ProductBatchMapper extends BaseMapper<ProductBatch> {

  @Select("SELECT * FROM product_batch WHERE batch_no = #{batchNo}")
  ProductBatch findByBatchNo(@Param("batchNo") String batchNo);
}

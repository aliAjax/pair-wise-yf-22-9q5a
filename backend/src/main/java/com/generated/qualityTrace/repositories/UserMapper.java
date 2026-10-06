package com.generated.qualityTrace.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.generated.qualityTrace.models.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

  @Select("SELECT * FROM app_user WHERE username = #{username}")
  User findByUsername(@Param("username") String username);
}

package com.ailu.server.mapper;

import com.ailu.entity.UserActive;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/24 上午12:46
 */
@Mapper
public interface UserActiveMapper {
    @Select("select * from user_active where user_id = #{userId}")
    UserActive getUserActive(Long userId);
}

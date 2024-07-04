package com.ailu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/18 下午11:20
 */
@Mapper
public interface SettingMapper {

    @Select("select background from setting where user_id = #{userId}")
    String getImages(Long userId);

    @Update("update setting set background = #{images} where user_id = #{userId}")
    void updateSetting(Long userId, String images);

    @Insert("insert into setting(user_id) values(#{userId})")
    void saveSetting(Long userId);
}

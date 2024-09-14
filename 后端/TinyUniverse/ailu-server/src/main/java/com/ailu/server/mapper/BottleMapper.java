package com.ailu.server.mapper;

import com.ailu.entity.Bottle;
import com.ailu.server.aop.AutoFill;
import com.ailu.server.aop.InsertOrUpdate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/9/2 下午1:41
 */

@Mapper
public interface BottleMapper {
    //获取最早的一个漂流瓶
    Bottle getBottle(List<Long> bottleIds,Long userId);

    @AutoFill(InsertOrUpdate.INSERT)
    @Insert("insert into bottle(user_id,title,content,is_public,create_time,update_time,parent_id) " +
            "values(#{userId},#{title},#{content},#{isPublic},#{createTime},#{updateTime},#{parentId})")
    @Options(useGeneratedKeys = true,keyProperty = "id", keyColumn = "id")
    void saveBottle(Bottle bottle);


    List<Bottle> getBottles(Integer type,List<Long> bottleIds);

    @Select("select b.id,u.id as user_id,u.username,b.title,b.content,b.is_public from bottle b,user u " +
            "where b.user_id = #{userId} and b.parent_id is null and b.user_id = u.id")
    List<Bottle> getBottlesByUserId(Long userId);

    @Select("select b.id,u.username,u.id as user_id,b.content from bottle b,user u " +
            "where b.parent_id = #{parentId} and b.user_id = u.id")
    List<Bottle> getBottlesByParentId(Long parentId);
}

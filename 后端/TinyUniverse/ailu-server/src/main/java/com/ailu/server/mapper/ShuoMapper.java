package com.ailu.server.mapper;

import com.ailu.dto.shuo.ShuoDTO;
import com.ailu.entity.Shuo;
import com.ailu.server.aop.AutoFill;
import com.ailu.server.aop.InsertOrUpdate;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/11 下午1:41
 */

@Mapper
public interface ShuoMapper {
    // TODO:使用{}实现动态SQL
    @Select({
            "SELECT * FROM shuo",
            "<if test='type != null'>",
            "ORDER BY ",
            "<choose>",
            "<when test='type == 1'>create_time DESC</when>",
            "<when test='type == 0'>love DESC</when>",
            "</choose>",
            "</if>",
     })
    Page<Shuo> pageQueryShuo(Integer type);

    @Select("SELECT * FROM shuo WHERE id = #{id}")
    Shuo getShuo(Long id);

    @Insert("insert into shuo(user_id,content,is_public,create_time,update_time)" +
            "values(#{userId},#{content},#{isPublic},#{createTime},#{updateTime})")
    @AutoFill(InsertOrUpdate.INSERT)
    void saveShuo(ShuoDTO shuoDTO);

    @Delete("delete from shuo where id = #{id}")
    void deleteShuo(Long id);

    @Update("update shuo set content = #{content},is_publish = #{isPublic},update_time = #{updateTime}")
    @AutoFill(InsertOrUpdate.UPDATE)
    void updateShuo(ShuoDTO shuoDTO);
}

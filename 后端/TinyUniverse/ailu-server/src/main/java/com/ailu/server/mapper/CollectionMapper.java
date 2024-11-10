package com.ailu.server.mapper;

import com.ailu.dto.collection.CollectionDTO;
import com.ailu.entity.Collections;
import com.ailu.server.aop.AutoFill;
import com.ailu.server.aop.InsertOrUpdate;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/23 下午8:52
 */

@Mapper
public interface CollectionMapper {
    //1是收藏夹
    @Select("select * from collection where type = 1 and user_id = #{userId} order by create_time desc")
    List<Collections> getAllCollections(Long userId);

    @AutoFill(InsertOrUpdate.INSERT)
    @Insert("insert into collection(name,user_id,article_id,is_public,type,parent_id,create_time,update_time) " +
            "values(#{name},#{userId},#{articleId},#{isPublic},#{type},#{parentId},#{createTime},#{updateTime})")
    void saveCollection(CollectionDTO collectionDTO);

    @Select("select * from collection where parent_id = #{parentId} and user_id = #{currentId} order by create_time")
    List<Collections> getAllCollection(Long parentId, Long currentId);

    @Delete("delete from collection where id = #{id} or parent_id = #{id}")
    void deleteCollections(Long id);

    @Delete("delete from collection where id = #{id}")
    void deleteCollection(Long id);

    @Select("select 1 from collection where name = #{name} limit 1")
    Integer isExist(String name);

    @AutoFill(InsertOrUpdate.UPDATE)
    @Update("update collection set name = #{name},is_public = #{isPublic},update_time = #{updateTime} where id = #{id}")
    void updateCollection(CollectionDTO collectionDTO);

    @Delete("delete from collection where article_id = #{articleId} and user_id = #{userId}")
    void deleteCollectionByArticleId(Long articleId,Long userId);

    @AutoFill(InsertOrUpdate.INSERT)
    void saveMultCollection(CollectionDTO collectionDTO);
}

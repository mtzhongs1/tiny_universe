package com.ailu.server.mapper;

import com.ailu.entity.KnowledgeBase;
import com.ailu.entity.KnowledgeBaseItem;
import com.ailu.entity.MyFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/10/14 下午1:23
 */
@Mapper
public interface KnowledgeMapper {
    @Insert("insert into knowledge_base_item(user_id,uuid,path,ext) values(#{userId},#{uuid},#{path},#{ext})")
    void insert(MyFile file);

    @Select("select * from knowledge_base_item where uuid = #{kbUuid}")
    KnowledgeBaseItem getKnowledgeBaseItem(String kbUuid);

    @Select("select * from knowledge_base_where id = #{knowledgeBaseId}")
    KnowledgeBase getKnowledgeBase(Long knowledgeBaseId);
}

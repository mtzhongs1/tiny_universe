package com.ailu.mapper;

import com.ailu.entity.Tag;
import com.ailu.vo.article.TagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 16/6/2024 下午8:59
 */
@Mapper
public interface TagMapper {
    @Select("select id,name,count from tag where name like concat(#{name},'%')")
    List<TagVO> getTags(String name);

    void addTagCount(List<Integer> tagIds);

    @Select("select name from tag t,article_tag at where t.id = at.tag_id and at.article_id = #{articleId} ")
    List<String> getTagNames(Long articleId);

}

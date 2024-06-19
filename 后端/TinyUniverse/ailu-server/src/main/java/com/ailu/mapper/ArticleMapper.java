package com.ailu.mapper;

import com.ailu.aop.AutoFill;
import com.ailu.aop.InsertOrUpdate;
import com.ailu.entity.Article;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: ailu
 * @description: TODO
 * @date: 12/6/2024 下午4:30
 */

@Mapper
public interface ArticleMapper {
    @AutoFill(InsertOrUpdate.INSERT)
    @Insert("insert into article(user_id,content,title,description,cover,tag,type,create_time,update_time)" +
            "values(#{userId},#{content},#{title},#{description},#{cover},#{tag},#{type},#{createTime},#{updateTime})")
    void saveArticle(Article article);
}

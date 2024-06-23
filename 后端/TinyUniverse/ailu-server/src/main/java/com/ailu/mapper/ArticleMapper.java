package com.ailu.mapper;

import com.ailu.aop.AutoFill;
import com.ailu.aop.InsertOrUpdate;
import com.ailu.entity.Article;
import com.ailu.vo.article.ArticleVO;
import com.github.pagehelper.Page;
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
    void saveArticle(Article article);

    Page<ArticleVO> pageQueryArticle(Long userId);
}

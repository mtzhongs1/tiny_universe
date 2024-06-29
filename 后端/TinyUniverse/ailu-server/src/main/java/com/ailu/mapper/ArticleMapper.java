package com.ailu.mapper;

import com.ailu.aop.AutoFill;
import com.ailu.aop.InsertOrUpdate;
import com.ailu.dto.article.ArticleDTO;
import com.ailu.entity.Article;
import com.ailu.vo.article.ArticleAndActiveVO;
import com.ailu.vo.article.ArticleVO;
import com.ailu.vo.article.DraftVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 12/6/2024 下午4:30
 */

@Mapper
public interface ArticleMapper {
    @AutoFill(InsertOrUpdate.INSERT)
    void saveArticle(Article article);

    Page<ArticleAndActiveVO> pageQueryArticle(Long userId);

    ArticleVO getArticle(Long articleId);

    Page<DraftVO> pageQueryDraft(Long userId,String name);

    void deleteArtOrDra(List<Long> ids,Integer type);

    @AutoFill(InsertOrUpdate.UPDATE)
    @Update("update article set title = #{title},content = #{content},description = #{description},cover=#{cover},update_time = #{updateTime} where id = #{id}")
    void updateArticle(ArticleDTO articleDTO);
}

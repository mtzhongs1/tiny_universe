package com.ailu.server.mapper;

import com.ailu.dto.article.ArticleDTO;
import com.ailu.dto.article.ArticlePageDTO;
import com.ailu.dto.article.ArticleTextDTO;
import com.ailu.dto.user.UserActiveVO;
import com.ailu.entity.Article;
import com.ailu.server.aop.AutoFill;
import com.ailu.server.aop.InsertOrUpdate;
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

    Page<ArticleAndActiveVO> pageQueryArticle(ArticlePageDTO articlePageDTO);

    ArticleVO getArticle(Long articleId);

    Page<DraftVO> pageQueryDraft(Long userId,String name);

    void deleteArtOrDra(List<Long> ids,Integer type);

    @AutoFill(InsertOrUpdate.UPDATE)
    @Update("update article set title = #{title},content = #{content},description = #{description},cover=#{cover},update_time = #{updateTime} where id = #{id}")
    void updateArticle(ArticleDTO articleDTO);

    @Select("select id,author,content,title from article")
    List<ArticleTextDTO> getArticles();

    Page<ArticleAndActiveVO> getArticlesByIds(List<Long> articleIds,int type);

    @Select("select u.id as user_id,u.avatar as avatar from article a,user u where a.id = #{articleId} and a.user_id = u.id")
    UserActiveVO getUserIdByArticleId(Long articleId);
}

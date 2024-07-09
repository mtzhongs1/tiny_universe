package com.ailu.server.service.article;

import com.ailu.dto.article.ArticleDTO;
import com.ailu.dto.user.UserActiveVO;
import com.ailu.entity.Article;
import com.ailu.result.PageResult;
import com.ailu.vo.article.ArticleVO;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 12/6/2024 下午4:20
 */


public interface ArticleService {
    void publishArticle(Article article);

    PageResult pageQueryArticle(Long userId, int pageNum, int pageSize,int type);

    ArticleVO getArticle(Long articleId);

    void deleteArticle(List<Long> ids);

    void updateArticle(ArticleDTO articleDTO);

    PageResult search(String name,int pageNum,int pageSize,int type);

    PageResult pageQueryAllArticle(int pageNum, int pageSize,int type);

    UserActiveVO getUser(Long articleId);

}

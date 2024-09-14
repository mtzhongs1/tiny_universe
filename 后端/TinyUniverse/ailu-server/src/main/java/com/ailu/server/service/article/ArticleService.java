package com.ailu.server.service.article;

import com.ailu.dto.article.ArticleDTO;
import com.ailu.dto.article.ArticlePageDTO;
import com.ailu.dto.user.UserActiveVO;
import com.ailu.entity.Article;
import com.ailu.result.PageResult;
import com.ailu.vo.article.ArticleVO;
import org.apache.mahout.cf.taste.common.TasteException;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 12/6/2024 下午4:20
 */


public interface ArticleService {
    void publishArticle(Article article);

    PageResult pageQueryArticle(ArticlePageDTO articlePageDTO) throws TasteException;

    ArticleVO getArticle(Long articleId);

    void deleteArticle(List<Long> ids);

    void updateArticle(ArticleDTO articleDTO);

    PageResult search(String name,int pageNum,int pageSize,int type);

    UserActiveVO getUser(Long articleId);

}

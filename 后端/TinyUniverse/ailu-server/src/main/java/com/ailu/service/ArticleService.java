package com.ailu.service;

import com.ailu.dto.article.ArticleDTO;
import com.ailu.entity.Article;
import com.ailu.vo.article.ArticleVO;
import org.aspectj.weaver.Lint;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 12/6/2024 下午4:20
 */


public interface ArticleService {
    void publishArticle(Article article);

    List<ArticleVO> pageQueryArticle(Long userId, int pageNum, int pageSize);
}

package com.ailu.service;

import com.ailu.dto.article.ArticleDTO;
import com.ailu.entity.Article;

/**
 * @author: ailu
 * @description: TODO
 * @date: 12/6/2024 下午4:20
 */


public interface ArticleService {
    void publishArticle(ArticleDTO articleDTO);
}

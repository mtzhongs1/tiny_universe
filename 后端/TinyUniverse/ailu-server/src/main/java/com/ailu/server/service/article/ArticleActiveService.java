package com.ailu.server.service.article;

import com.ailu.entity.ArticleActive;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/22 上午12:47
 */

public interface ArticleActiveService {
    boolean love(Long articleId);

    void forward();

    void watch(Long articleId);

    boolean collection(Long articleId);

    ArticleActive getArticleActive(Long articleId);
}

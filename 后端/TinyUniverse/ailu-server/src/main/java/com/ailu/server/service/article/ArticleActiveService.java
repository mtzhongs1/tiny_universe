package com.ailu.server.service.article;

import com.ailu.dto.collection.ColDTO;
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

    void collection(ColDTO colDTO);

    ArticleActive getArticleActive(Long articleId);

    void delCollection(Long articleId);
}

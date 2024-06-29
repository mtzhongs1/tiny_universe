package com.ailu.service.article;

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
}

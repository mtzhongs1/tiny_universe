package com.ailu.server.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/21 上午12:22
 */

@Mapper
public interface ArticleActiveMapper {
    void getArticleActive(List<Long> articleIds);
}

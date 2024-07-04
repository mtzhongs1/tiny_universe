package com.ailu.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/20 下午8:28
 */

@Mapper
public interface ArticleTagMapper {

    void saveArticleTag(Long articleId, List<Integer> tagIds);
}

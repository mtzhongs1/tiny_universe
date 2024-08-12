package com.ailu.server.service.article;

import com.ailu.vo.article.TagVO;

import java.util.List;
import java.util.Set;

/**
 * @author: ailu
 * @description: TODO
 * @date: 16/6/2024 下午8:56
 */

public interface TagService {
    List<TagVO> getTags(String name);
    List<String> getTagNames(Long ArticleId);
    Set<Long> getArticleIdById(Integer tagId);

}

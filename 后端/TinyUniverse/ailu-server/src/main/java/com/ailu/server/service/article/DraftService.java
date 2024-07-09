package com.ailu.server.service.article;

import com.ailu.dto.article.DraftDTO;
import com.ailu.entity.Article;
import com.ailu.result.PageResult;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/26 上午1:18
 */

public interface DraftService {
    PageResult pageQueryDraft(DraftDTO draftDTO);

    void deleteDraft(List<Long> ids);

    void saveDraft(Article article);

}

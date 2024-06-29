package com.ailu.service.article;

import com.ailu.dto.article.DraftDTO;
import com.ailu.vo.article.DraftVO;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/26 上午1:18
 */

public interface DraftService {
    List<DraftVO> pageQueryDraft(DraftDTO draftDTO);

    void deleteDraft(List<Long> ids);
}

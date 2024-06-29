package com.ailu.service.impl.article;

import com.ailu.dto.article.DraftDTO;
import com.ailu.mapper.ArticleMapper;
import com.ailu.service.article.DraftService;
import com.ailu.vo.article.DraftVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/26 上午1:18
 */

@Service
public class DraftServiceImpl implements DraftService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<DraftVO> pageQueryDraft(DraftDTO draftDTO) {
        PageHelper.startPage(draftDTO.getPageNum(), draftDTO.getPageSize());
        Page<DraftVO> page = articleMapper.pageQueryDraft(draftDTO.getUserId(),draftDTO.getName());
        List<DraftVO> result = page.getResult();
        return result;
    }

    @Override
    public void deleteDraft(List<Long> ids) {
        articleMapper.deleteArtOrDra(ids,0);
    }
}

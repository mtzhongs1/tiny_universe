package com.ailu.service.impl.article;

import com.ailu.context.BaseContext;
import com.ailu.dto.article.DraftDTO;
import com.ailu.entity.Article;
import com.ailu.exception.BaseException;
import com.ailu.mapper.ArticleMapper;
import com.ailu.mapper.ArticleTagMapper;
import com.ailu.mapper.TagMapper;
import com.ailu.result.PageResult;
import com.ailu.service.article.DraftService;
import com.ailu.vo.article.DraftVO;
import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/26 上午1:18
 */

@Service
public class DraftServiceImpl implements DraftService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public PageResult pageQueryDraft(DraftDTO draftDTO) {
        PageHelper.startPage(draftDTO.getPageNum(), draftDTO.getPageSize());
        Page<DraftVO> page = articleMapper.pageQueryDraft(draftDTO.getUserId(),draftDTO.getName());
        List<DraftVO> result = page.getResult();
        return new PageResult(page.getTotal(),result);
    }

    @Override
    public void deleteDraft(List<Long> ids) {
        articleMapper.deleteArtOrDra(ids,0);
    }

    @Override
    @Transactional
    public void saveDraft(Article article) {
        if(StringUtils.isEmpty(article.getTag())){
            throw new BaseException("标签为空");
        }
        Long userId = BaseContext.getCurrentId();
        article.setUserId(userId);
        articleMapper.saveArticle(article);
        Long articleId = article.getId();
        String[] tags = article.getTag().split(",");
        List<Integer> tagIds = Arrays.stream(tags).map(Integer::parseInt).collect(Collectors.toList());
        tagMapper.addTagCount(tagIds);
        // 修改article_tag
        articleTagMapper.saveArticleTag(articleId,tagIds);
    }
}

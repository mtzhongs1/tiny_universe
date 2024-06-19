package com.ailu.service.impl.article;

import com.ailu.context.BaseContext;
import com.ailu.dto.article.ArticleDTO;
import com.ailu.entity.Article;
import com.ailu.mapper.ArticleMapper;
import com.ailu.service.ArticleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 12/6/2024 下午4:20
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void publishArticle(ArticleDTO articleDTO) {
        articleDTO.setUserId(BaseContext.getCurrentId());
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO,article);
        articleMapper.saveArticle(article);

    }
}

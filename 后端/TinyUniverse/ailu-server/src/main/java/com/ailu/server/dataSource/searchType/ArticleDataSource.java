package com.ailu.server.dataSource.searchType;

import com.ailu.result.PageResult;
import com.ailu.server.service.article.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/8 下午5:23
 */
@Service
public class ArticleDataSource implements DataSource{

    @Autowired
    private ArticleService articleService;

    @Override
    public PageResult search(String content, Integer pageNum, Integer pageSize, Integer type) {
        return articleService.search(content,pageNum,pageSize,type);
    }
}

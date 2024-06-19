package com.ailu.controller.article;

import com.ailu.dto.article.ArticleDTO;
import com.ailu.entity.Article;
import com.ailu.result.Result;
import com.ailu.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: ailu
 * @Date: 12/6/2024 下午4:16
 */

@RestController
@RequestMapping("/article")
@Api(tags = "文章系统")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/publish")
    @ApiOperation(value = "发布文章")
    public Result publishArticle(@RequestBody ArticleDTO articleDTO) {
        articleService.publishArticle(articleDTO);
        return Result.success();
    }

}

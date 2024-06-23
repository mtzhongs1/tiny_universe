package com.ailu.controller.article;

import com.ailu.dto.article.ArticleDTO;
import com.ailu.entity.Article;
import com.ailu.result.PageResult;
import com.ailu.result.Result;
import com.ailu.service.ArticleService;
import com.ailu.vo.article.ArticleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    public Result publishArticle(@RequestBody Article article) {
        articleService.publishArticle(article);
        return Result.success();
    }

    @GetMapping("/page/{userId}/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页获取文章信息")
    public Result<PageResult> pageQueryArticle(@PathVariable Long userId, @PathVariable int pageNum,
                                   @PathVariable int pageSize){
        List<ArticleVO> articles = articleService.pageQueryArticle(userId,pageNum, pageSize);
        PageResult pageResult = new PageResult();
        pageResult.setRecords(articles);
        pageResult.setTotal(articles.size());
        return Result.success(pageResult);
    }


}

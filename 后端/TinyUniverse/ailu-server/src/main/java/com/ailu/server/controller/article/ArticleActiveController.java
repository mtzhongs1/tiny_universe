package com.ailu.server.controller.article;

import com.ailu.entity.ArticleActive;
import com.ailu.result.Result;
import com.ailu.server.service.article.ArticleActiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/22 上午12:43
 */
@RestController
@RequestMapping("/article_active")
@Api(tags = "文章活动系统")
public class ArticleActiveController {

    @Autowired
    private ArticleActiveService articleActiveService;

    @GetMapping("/{articleId}")
    public Result<ArticleActive> getArticleActive(@PathVariable Long articleId){
        ArticleActive articleActive = articleActiveService.getArticleActive(articleId);
        return Result.success(articleActive);
    }

    // 收藏
    @PostMapping("/love")
    @ApiOperation("点赞")
    public Result love(@RequestParam("articleId") Long articleId){
        boolean isAdd = articleActiveService.love(articleId);
        return Result.success(isAdd);
    }

    @PostMapping("/forward")
    public Result forward(){
        articleActiveService.forward();
        return Result.success();
    }
    @PostMapping("/comment")
    public Result comment(){
        return Result.success();
    }
    @PostMapping("/watch")
    public Result watch(@RequestParam("articleId") Long articleId){
        articleActiveService.watch(articleId);
        return Result.success();
    }
    @PostMapping("/collection")
    public Result collection(@RequestParam("articleId") Long articleId){
        boolean isAdd = articleActiveService.collection(articleId);
        return Result.success(isAdd);
    }

}

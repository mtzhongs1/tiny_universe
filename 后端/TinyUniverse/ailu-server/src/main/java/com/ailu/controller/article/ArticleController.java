package com.ailu.controller.article;

import com.ailu.dto.article.ArticleDTO;
import com.ailu.entity.Article;
import com.ailu.result.PageResult;
import com.ailu.result.Result;
import com.ailu.service.article.ArticleService;
import com.ailu.service.article.TagService;
import com.ailu.vo.article.ArticleAndActiveVO;
import com.ailu.vo.article.ArticleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
    @Autowired
    private RedisCacheManager cacheManager;


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
        List<ArticleAndActiveVO> articles = articleService.pageQueryArticle(userId,pageNum, pageSize);
        PageResult pageResult = new PageResult();
        pageResult.setRecords(articles);
        pageResult.setTotal(articles.size());
        return Result.success(pageResult);
    }

    @GetMapping("/{articleId}")
    @Cacheable(value= "article",key = "#articleId")
    public Result<ArticleVO> getArticle(@PathVariable Long articleId){
        ArticleVO articleVO = articleService.getArticle(articleId);
        return Result.success(articleVO);
    }

    @DeleteMapping
    @ApiOperation("删除文章")
    public Result deleteArticle(@RequestBody List<Long> ids){
        // TODO:删除集合中的缓存
        for(Long id : ids){
            Objects.requireNonNull(cacheManager.getCache("article")).evictIfPresent(id);
        }
        articleService.deleteArticle(ids);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改文章")
    @CacheEvict(value = "article",key = "#articleDTO.id")
    public Result updateArticle(@RequestBody ArticleDTO articleDTO){
        articleService.updateArticle(articleDTO);
        return Result.success();
    }

}

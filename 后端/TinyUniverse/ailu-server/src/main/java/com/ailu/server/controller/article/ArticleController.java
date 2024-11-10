package com.ailu.server.controller.article;

import com.ailu.aop.Log;
import com.ailu.aop.OperationType;
import com.ailu.dto.article.ArticleDTO;
import com.ailu.dto.article.ArticlePageDTO;
import com.ailu.dto.user.UserActiveVO;
import com.ailu.entity.Article;
import com.ailu.result.PageResult;
import com.ailu.result.Result;
import com.ailu.server.dataSource.searchType.DataSourceFactory;
import com.ailu.server.service.article.ArticleService;
import com.ailu.vo.article.ArticleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private DataSourceFactory dataSourceFactory;
    // @Autowired
    // private RedisCacheManager cacheManager;

    @PostMapping("/publish")
    @ApiOperation(value = "发布文章")
    // @CacheEvict(value = "article_all",allEntries = true)
    public Result publishArticle(@RequestBody Article article) {
        articleService.publishArticle(article);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页获取用户文章信息")
    // @Cacheable(value = "article_all",key = "'userId:'+#articlePageDTO.userId+':' "+
    //         "+'pageNum:'+#articlePageDTO.pageNum+':'"+
    //         "+'pageSize:'+#articlePageDTO.pageSize+':'"+
    //         "+'type:'+#articlePageDTO.type+':'"+
    //         "+'tag:'+#articlePageDTO.tag")
    //type=0：热度排名 type=1，最新 type=2，推荐
    public Result<PageResult> pageQueryArticle(ArticlePageDTO articlePageDTO) throws TasteException {
        PageResult pageResult = articleService.pageQueryArticle(articlePageDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{articleId}")
    public Result<ArticleVO> getArticle(@PathVariable Long articleId){
        ArticleVO articleVO = articleService.getArticle(articleId);
        return Result.success(articleVO);
    }

    @DeleteMapping
    @ApiOperation("删除文章")
    public Result deleteArticle(@RequestBody List<Long> ids){
        // TODO:删除集合中的缓存
        // for(Long id : ids){
        //     Objects.requireNonNull(cacheManager.getCache("article")).evictIfPresent(id);
        // }
        articleService.deleteArticle(ids);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改文章")
    public Result updateArticle(@RequestBody ArticleDTO articleDTO){
        articleService.updateArticle(articleDTO);
        return Result.success();
    }

    @GetMapping("/search/{pageNum}/{pageSize}/{type}/{content}")
    @ApiOperation("分词搜索")
    @Log(title = "搜索", operationType = OperationType.OTHER)
    public Result<PageResult> search(@PathVariable String content,@PathVariable Integer pageNum,
                                     @PathVariable Integer pageSize,@PathVariable Integer type){
        PageResult page = dataSourceFactory.getDataSource("article").search(content,pageNum,pageSize,type);
        return Result.success(page);
    }


    @ApiOperation("根据文章id获取用户信息")
    @GetMapping("/getUser/{articleId}")
    public Result<UserActiveVO> getUser(@PathVariable Long articleId){
        UserActiveVO userActiveVO = articleService.getUser(articleId);
        return Result.success(userActiveVO);
    }

}

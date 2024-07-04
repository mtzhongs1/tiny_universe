package com.ailu.controller.article;

import com.ailu.dto.article.DraftDTO;
import com.ailu.entity.Article;
import com.ailu.result.PageResult;
import com.ailu.result.Result;
import com.ailu.service.article.ArticleService;
import com.ailu.service.article.DraftService;
import com.ailu.vo.article.DraftVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/26 上午1:06
 */
@RestController
@RequestMapping("/draft")
@Api(tags="草稿系统")
public class DraftController {

    @Autowired
    private DraftService draftService;

    @GetMapping("/page")
    @ApiOperation("分页查询草稿")
    @Cacheable(value = "draft", key = "'draft:' + #draftDTO.userId  + ':' + #draftDTO.name + ':' + #draftDTO.pageNum + ':' + #draftDTO.pageSize")
    public Result<PageResult> pageQueryDraft(DraftDTO draftDTO){
        PageResult pageResult = draftService.pageQueryDraft(draftDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除草稿")
    @CacheEvict(value = "draft", allEntries = true)
    public Result deleteDraft(@RequestBody List<Long> ids){
        draftService.deleteDraft(ids);
        return Result.success();
    }

    @PostMapping
    @ApiOperation("保存草稿")
    @CacheEvict(value = "draft", allEntries = true)
    public Result saveDraft(@RequestBody Article article){
        draftService.saveDraft(article);
        return Result.success();
    }


}

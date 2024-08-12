package com.ailu.server.controller.comment;

import com.ailu.dto.comment.CommentDTO;
import com.ailu.dto.comment.CommentUpdateDTO;
import com.ailu.dto.comment.CommentVO;
import com.ailu.result.PageResult;
import com.ailu.result.Result;
import com.ailu.server.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/4 上午11:54
 */
@RequestMapping("/comment")
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/comments/{articleId}/{type}")
    public Result<PageResult> getComments(@PathVariable Long articleId, @PathVariable int type) {
        PageResult commentTrees = commentService.getComments(articleId,type);
        return Result.success(commentTrees);
    }

    @PostMapping
    public Result saveComment(@RequestBody CommentDTO commentDTO){
        commentService.saveComment(commentDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteComment(@RequestParam Long articleId,@RequestParam Long id){
        commentService.deleteComment(articleId,id);
        return Result.success();
    }

    @PutMapping
    public Result updateComment(@RequestBody CommentUpdateDTO commentDTO){
        commentService.updateComment(commentDTO);
        return Result.success();
    }

    @PostMapping("/doLove")
    public Result<Boolean> doLove(@RequestParam Long id){
        Boolean isLove = commentService.doLove(id);
        return Result.success(isLove);
    }
}

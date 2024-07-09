package com.ailu.server.controller.comment;

import com.ailu.dto.comment.CommentDTO;
import com.ailu.dto.comment.CommentVO;
import com.ailu.result.Result;
import com.ailu.server.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Result<List<CommentVO>> getComments(@PathVariable Long articleId,@PathVariable int type) {
        List<CommentVO> commentTrees =  commentService.getComments(articleId,type);
        return Result.success(commentTrees);
    }

    @PostMapping
    public Result saveComment(@RequestBody CommentDTO commentDTO){
        commentService.saveComment(commentDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteComment(@RequestParam Long id){
        commentService.deleteComment(id);
        return Result.success();
    }

}

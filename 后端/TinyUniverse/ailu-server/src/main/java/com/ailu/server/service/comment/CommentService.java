package com.ailu.server.service.comment;

import com.ailu.dto.comment.CommentDTO;
import com.ailu.dto.comment.CommentUpdateDTO;
import com.ailu.result.PageResult;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/4 上午11:55
 */

public interface CommentService {
    PageResult getComments(Long articleId, int type);

    void saveComment(CommentDTO commentDTO);

    void deleteComment(Long articleId,Long id);

    void deleteCommentByArticleId(List<Long> articleIds);

    void updateComment(CommentUpdateDTO commentDTO);

    Boolean doLove(Long id);
}

package com.ailu.server.service.comment;

import com.ailu.dto.comment.CommentDTO;
import com.ailu.dto.comment.CommentVO;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/4 上午11:55
 */

public interface CommentService {
    List<CommentVO> getComments(Long articleId,int type);

    void saveComment(CommentDTO commentDTO);

    void deleteComment(Long id);

    void deleteCommentByArticleId(List<Long> articleIds);
}

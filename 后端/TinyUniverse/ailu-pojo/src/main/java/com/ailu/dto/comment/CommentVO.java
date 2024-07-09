package com.ailu.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/5 上午11:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private Long ArticleId;
    //回复的评论id
    private Long parentId;
    private String content;
    //toId为null时才有，其他文章的toId父祖为当前id
    private List<CommentVO> children;
    private Long createTime;
    private Long updateTime;
    private Long love;
}

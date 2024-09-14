package com.ailu.dto.comment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/5 上午11:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO implements Serializable{
    private static final long serialVersionUID = -465465464321L;
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private Long articleId;
    //回复的评论id
    private Long parentId;
    private String content;
    //toId为null时才有，其他文章的toId父祖为当前id
    private List<CommentVO> children;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long love;
    private Boolean isLove;
}

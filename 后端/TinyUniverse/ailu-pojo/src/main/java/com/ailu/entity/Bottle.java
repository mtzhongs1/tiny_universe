package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;

/**
 * (Bottle)实体类
 *
 * @author makejava
 * @since 2024-09-02 13:43:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bottle implements Serializable {
    private static final long serialVersionUID = -69766472623627300L;

    private Long id;

    private Long userId;

    private String username;

    private String content;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Long parentId;

    private Integer isPublic;

    private String title;


}


package com.ailu.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: ailu
 * @Date: 16/6/2024 下午9:00
 */

@Data
public class TagVO implements Serializable {
    private Long id;
    private String name;
    private Long count;
}

package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/14 下午12:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseItem {

    private Long id;
    private Long knowledgeBaseId;
    private String uuid;
    private String title;
    private Long userId;
    private String username;
    private String system;
}

package com.ailu.entity;

import lombok.Data;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/19 下午5:19
 */
@Data
public class KnowledgeBase {

    private Long id;
    private String username;
    private String system;


    //TODO:转换为向量后的维度数
    private Integer embeddingCount;
    // TODO:系统在响应查询时返回的最大文档数量限制
    private Integer ragMaxResults;
    //TODO:指只有得分超过此阈值的文档才会被包含在最终的搜索结果中
    private Double ragMinScore;
    //TODO:假设有一篇长文档，将其分割成多个小片段，并且每个片段之间有一定的重叠部分，这样可以确保每个片段都包含足够的上下文信息。
    private Integer ragMaxOverlap;
    private Double temperature;
}

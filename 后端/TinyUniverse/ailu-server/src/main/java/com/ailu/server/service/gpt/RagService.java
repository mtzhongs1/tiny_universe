package com.ailu.server.service.gpt;

import com.ailu.server.properties.RagProperties;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/17 下午5:27
 */
@Service
public class RagService {

    @Autowired
    private RagProperties ragProperties;

    private final int RAG_MAX_SEGMENT_SIZE_IN_TOKENS = 1000;

    private final int RAG_NUMBER_RETURN_MAX = 3;

    private final int RAG_MAX_RESULTS_DEFAULT = 3;

    /**
     * 创建一个内容检索器
     * 该方法根据给定的元数据条件、最大结果数量和最小分数阈值来构建一个内容检索器
     * 通过元数据条件过滤嵌入式存储中的内容，并使用嵌入式模型进行评分，以满足最小分数要求
     *
     * @param metadataCond 元数据条件映射，用于过滤内容
     * @param maxResults 最大结果数量，限制返回的结果集大小
     * @param minScore 最小分数阈值，只有分数大于等于该阈值的结果才会被返回
     * @return 返回一个ContentRetriever实例，该实例可以根据指定的条件和评分阈值从嵌入式存储中检索内容
     */
    public ContentRetriever createRetriever(Map<String, String> metadataCond, int maxResults, double minScore) {
        // 初始化过滤条件对象，用于后续的元数据条件过滤
        Filter filter = null;
        // 遍历元数据条件映射，构建复杂的过滤条件
        for (Map.Entry<String, String> entry : metadataCond.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 如果过滤条件为空，则初始化为当前键值对的等于条件
            if (null == filter) {
                filter = new IsEqualTo(key, value);
            } else {
                // 如果过滤条件已存在，则将当前键值对的等于条件与现有条件进行逻辑与操作，以构建复杂过滤条件
                filter = filter.and(new IsEqualTo(key, value));
            }
        }
        // 使用构建器模式构建ContentRetriever实例
        // 如果提供的最小分数阈值小于等于0，则使用默认的最小分数阈值，否则使用提供的最小分数阈值
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(ragProperties.getEmbeddingStore())
                .embeddingModel(ragProperties.getEmbeddingModel())
                .maxResults(maxResults)
                .minScore(minScore <= 0 ? 0.6 : minScore)
                .filter(filter)
                .build();
    }
    /**
     * 根据模型的contentWindow计算使用该模型最多召回的文档数量
     * 该方法首先计算用户问题的token长度，然后根据contentWindow大小和问题长度
     * 确定最多能召回的文档数量，以适应模型的输入限制
     *
     * @param userQuestion 用户问题，用于计算问题的token长度
     * @param contentWindow AI模型所能容纳的窗口大小，决定了总输入token数
     * @return 基于contentWindow和问题长度，最多召回的文档数量
     */
    public int getRetrieveMaxResults(String userQuestion, int contentWindow) {
        // 计算用户问题的token长度
        int questionLength = new OpenAiTokenizer(GPT_3_5_TURBO).estimateTokenCountInText(userQuestion);
        // 计算最多能召回的文档长度，确保不超过contentWindow
        int maxRetrieveDocLength = contentWindow - questionLength;
        // 如果最大召回文档长度小于最小段大小，则返回默认召回数量
        if (maxRetrieveDocLength < RAG_MAX_SEGMENT_SIZE_IN_TOKENS) {
            return RAG_MAX_RESULTS_DEFAULT;
        } else if (maxRetrieveDocLength > RAG_NUMBER_RETURN_MAX * RAG_MAX_SEGMENT_SIZE_IN_TOKENS) {
            // 如果最大召回文档长度大于最大段大小乘以最大返回数量，则返回最大返回数量
            return RAG_NUMBER_RETURN_MAX;
        } else {
            // 否则，计算并返回最大召回文档数量
            return maxRetrieveDocLength / RAG_MAX_SEGMENT_SIZE_IN_TOKENS;
        }
    }
}

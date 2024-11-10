package com.ailu.server.properties;

import cn.hutool.core.date.TemporalUtil;
import com.ailu.context.IndexNameContext;
import com.ailu.server.config.RedissonConfig;
import com.ailu.server.service.gpt.impl.GlmServiceImpl;
import com.zhipu.oapi.Constants;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/17 下午5:27
 */
@Component
@Data
public class RagProperties {
    // TODO:嵌入模型
    private EmbeddingModel embeddingModel;
    // TODO:嵌入存储
    private EmbeddingStore<TextSegment> embeddingStore;
    private String indexName;
    @Autowired
    private RedissonConfig redissonConfig;
    @Autowired
    private GlmServiceImpl glmServiceImpl;
    @Value("${gpt.glm.apiKey}")
    @Getter
    private String API_SECRET_KEY;

    @PostConstruct
    public void init(){
        String apiSecretKey = API_SECRET_KEY;
        embeddingModel = new ZhipuAiEmbeddingModel("https://open.bigmodel.cn/",apiSecretKey, Constants.ModelEmbedding2,1024,3,
                true,true, Duration.of(60000, ChronoUnit.MILLIS),Duration.of(60000, ChronoUnit.MILLIS),
                Duration.of(60000, ChronoUnit.MILLIS),Duration.of(60000, ChronoUnit.MILLIS));
        // embeddingModel = new ZhipuAiEmbeddingModel("https://open.bigmodel.cn/",apiSecretKey, Constants.ModelEmbedding2,3,true,true);
        embeddingStore = RedisEmbeddingStore.builder()
                .host(redissonConfig.getHost())
                .port(redissonConfig.getPort())
                // .user(redissonConfig.getUser())
                // .password(redissonConfig.getPassword())
                .metadataKeys(Collections.singletonList("uuid"))
                .dimension(1024)
                .build();
    }
}

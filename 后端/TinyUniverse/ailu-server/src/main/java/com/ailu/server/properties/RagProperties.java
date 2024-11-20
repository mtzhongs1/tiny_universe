package com.ailu.server.properties;


import com.ailu.server.config.RedissonConfig;
import com.ailu.server.service.gpt.impl.GlmServiceImpl;
import com.ailu.util.convert.StringUtils;
import com.zhipu.oapi.Constants;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ailu.server.properties.ModelProperties.QWEN_MAX;

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
    @Autowired
    private RedissonConfig redissonConfig;
    @Autowired
    private GlmServiceImpl glmServiceImpl;
    @Value("${gpt.qwen.apiKey}")
    @Getter
    private String API_SECRET_KEY;
    public void init(String indexName,String modelName){
        if(StringUtils.isEmpty(modelName)){
            modelName = QWEN_MAX;
        }
        // embeddingModel = new ZhipuAiEmbeddingModel("https://open.bigmodel.cn/",apiSecretKey, Constants.ModelEmbedding2,1024,3,
        //         true,true, Duration.of(60000, ChronoUnit.MILLIS),Duration.of(60000, ChronoUnit.MILLIS),
        //         Duration.of(60000, ChronoUnit.MILLIS),Duration.of(60000, ChronoUnit.MILLIS));
        // embeddingModel = new ZhipuAiEmbeddingModel("https://open.bigmodel.cn/",apiSecretKey, Constants.ModelEmbedding2,3,true,true);
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                // 非守护线程:确保任务不会因为JVM决定退出而导致任务中断
                thread.setDaemon(false);
                return thread;
            }
        };
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(4), threadFactory);
        embeddingModel = new AllMiniLmL6V2EmbeddingModel(threadPoolExecutor);
        embeddingStore = RedisEmbeddingStore.builder()
                .host(redissonConfig.getHost())
                .port(redissonConfig.getPort())
                // .user(redissonConfig.getUser())
                // .password(redissonConfig.getPassword())
                .metadataKeys(Collections.singletonList("uuid"))
                .indexName(indexName)
                .dimension(384)
                .build();
    }
}

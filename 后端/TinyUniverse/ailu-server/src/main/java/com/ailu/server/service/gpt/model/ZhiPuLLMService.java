package com.ailu.server.service.gpt.model;

import com.ailu.server.service.gpt.assistant.IChatAssistant;
import com.ailu.server.service.gpt.store.MapDBChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static com.ailu.server.properties.ModelProperties.GLM_4_FLASH;
import static com.ailu.server.properties.ModelProperties.ZHIPU_KEY;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/17 下午7:48
 */
@Accessors(chain = true)
@Slf4j
public class ZhiPuLLMService {

    protected ChatLanguageModel buildChatLLM(SseAskParams sseAskParams) {
        double temperature = 0.7;
        String model = GLM_4_FLASH;
        if (null != sseAskParams && sseAskParams.getTemperature() > 0 && sseAskParams.getTemperature() <= 1) {
            temperature = sseAskParams.getTemperature();
            if(StringUtils.isNotBlank(sseAskParams.getModelName())){
                model = sseAskParams.getModelName();
            }
        }
        return ZhipuAiChatModel.builder()
                .apiKey(ZHIPU_KEY)
                .model(model)
                .temperature(temperature)
                .maxRetries(1)
                .logRequests(true)
                .logResponses(true)
                .callTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .readTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .writeTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .connectTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .build();
    }

    protected StreamingChatLanguageModel buildStreamingChatLLM() {
        return ZhipuAiStreamingChatModel.builder()
                .apiKey(ZHIPU_KEY)
                .model(GLM_4_FLASH)
                .temperature(0.7)
                .topP(1.0)
                .logRequests(true)
                .logResponses(true)
                .callTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .readTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .writeTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .connectTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .build();
    }

    public void ragChat(ContentRetriever retriever,SseAskParams sseAskParams) {
        // TODO:创建聊天记忆提供者，用于管理聊天历史记录
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(6)
                .chatMemoryStore(MapDBChatMemoryStore.getSingleton())
                .build();
        // TODO:创建查询转换器，用于修改或扩展原始 Query来提高检索质量，这里使用将聊天记忆（以前的对话历史记录）压缩为简洁 Query的转换器
        QueryTransformer queryTransformer = new CompressingQueryTransformer(buildChatLLM(sseAskParams));
        // TODO:创建检索增强器，用于增强提示生成过程中的内容检索,作为 RAG 流程的入口点
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(retriever)
                .build();
        // TODO:构建聊天助理，包括流式聊天语言模型、检索增强器和聊天记忆提供者
        IChatAssistant assistant = AiServices.builder(IChatAssistant.class)
                .streamingChatLanguageModel(buildStreamingChatLLM())
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
        AssistantChatParams assistantChatParams = sseAskParams.getAssistantChatParams();
        // TODO: AI 服务中的返回类型。
        TokenStream tokenStream;
        // 根据是否有系统消息，选择不同的聊天方法
        if (StringUtils.isNotBlank(assistantChatParams.getSystemMessage())) {
            tokenStream = assistant.chat(assistantChatParams.getMessageId(), assistantChatParams.getSystemMessage(), assistantChatParams.getUserMessage());
        } else {
            tokenStream = assistant.chatWithoutSystemMessage(assistantChatParams.getMessageId(), assistantChatParams.getUserMessage());
        }
        registerTokenStreamCallBack(tokenStream, sseAskParams);
    }

    public void registerTokenStreamCallBack(TokenStream tokenStream, SseAskParams sseAskParams) {
        SseEmitter sseEmitter = sseAskParams.getSseEmitter();
        tokenStream
                .onNext((content) -> {
                    log.info(content);
                    //加空格配合前端的fetchEventSource进行解析，见https://github.com/Azure/fetch-event-source/blob/45ac3cfffd30b05b79fbf95c21e67d4ef59aa56a/src/parse.ts#L129-L133
                    try {
                        sseEmitter.send(content);
                    } catch (IOException e) {
                        log.error("stream onNext error", e);
                    }
                })
                .onComplete((response) -> {
                    log.info("返回数据结束了:{}", response);
                    sseEmitter.complete();
                })
                .onError((error) -> {
                    log.error("stream error", error);
                })
                .start();
    }

    public String nragChat(ContentRetriever retriever,SseAskParams sseAskParams) {
        // TODO:创建聊天记忆提供者，用于管理聊天历史记录
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(6)
                .chatMemoryStore(MapDBChatMemoryStore.getSingleton())
                .build();
        // TODO:创建查询转换器，用于修改或扩展原始 Query来提高检索质量，这里使用将聊天记忆（以前的对话历史记录）压缩为简洁 Query的转换器
        QueryTransformer queryTransformer = new CompressingQueryTransformer(buildChatLLM(sseAskParams));
        // TODO:创建检索增强器，用于增强提示生成过程中的内容检索,作为 RAG 流程的入口点
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(retriever)
                .build();
        // TODO:构建聊天助理，包括流式聊天语言模型、检索增强器和聊天记忆提供者
        IChatAssistant assistant = AiServices.builder(IChatAssistant.class)
                // .streamingChatLanguageModel(buildStreamingChatLLM())
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemoryProvider(chatMemoryProvider)
                .chatLanguageModel(buildChatLLM(sseAskParams))
                .build();
        AssistantChatParams assistantChatParams = sseAskParams.getAssistantChatParams();
        // TODO: AI 服务中的返回类型。
        String res;
        // 根据是否有系统消息，选择不同的聊天方法
        if (StringUtils.isNotBlank(assistantChatParams.getSystemMessage())) {
            res = assistant.nchat(assistantChatParams.getMessageId(), assistantChatParams.getSystemMessage(), assistantChatParams.getUserMessage());
        } else {
            res = assistant.nchatWithoutSystemMessage(assistantChatParams.getMessageId(), assistantChatParams.getUserMessage());
        }
        return res;
    }
}

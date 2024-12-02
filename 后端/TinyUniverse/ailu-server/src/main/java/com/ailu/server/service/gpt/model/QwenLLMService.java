package com.ailu.server.service.gpt.model;

import com.ailu.server.service.gpt.assistant.IChatAssistant;
import com.ailu.server.service.gpt.store.MapDBChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenStreamingChatModel;
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

import static com.ailu.server.properties.ModelProperties.*;
import static com.ailu.server.service.gpt.impl.QwenServiceImpl.getSseEmitter;

/**
 * @Description: 通义千问
 * @Author: ailu
 * @Date: 2024/10/17 下午7:48
 */
@Accessors(chain = true)
@Slf4j
public class QwenLLMService {

    public static ChatLanguageModel buildChatLLM(SseAskParams sseAskParams) {
        double temperature = 0.95;
        String model = QWEN_MAX;
        if (null != sseAskParams && sseAskParams.getTemperature() > 0 && sseAskParams.getTemperature() <= 1) {
            temperature = sseAskParams.getTemperature();
            if(StringUtils.isNotBlank(sseAskParams.getModelName())){
                model = sseAskParams.getModelName();
            }
        }
        return QwenChatModel.builder()
                .apiKey(QWEN_KEY)
                .temperature((float) temperature)
                .modelName(model)
                .enableSearch(true)
                .build();

    }

    public static StreamingChatLanguageModel buildStreamingChatLLM(SseAskParams sseAskParams) {
        double temperature = 0.95;
        String model = QWEN_MAX;
        if (null != sseAskParams && sseAskParams.getTemperature() > 0 && sseAskParams.getTemperature() <= 1) {
            temperature = sseAskParams.getTemperature();
            if(StringUtils.isNotBlank(sseAskParams.getModelName())){
                model = sseAskParams.getModelName();
            }
        }
        return QwenStreamingChatModel.builder()
                .apiKey(QWEN_KEY)
                .temperature((float) temperature)
                .modelName(model)
                .enableSearch(true)
                .build();
    }

    public static ChatMemoryProvider buildChatMemoryProvider(){
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(6)
                .chatMemoryStore(MapDBChatMemoryStore.getSingleton())
                .build();
    }

    public static void registerTokenStreamCallBack(TokenStream tokenStream, SseAskParams sseAskParams) {
        StringBuilder sb = new StringBuilder();
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

    public void nragChat(ContentRetriever retriever,SseAskParams sseAskParams) {
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
                .streamingChatLanguageModel(buildStreamingChatLLM(sseAskParams))
                .build();
        AssistantChatParams assistantChatParams = sseAskParams.getAssistantChatParams();
        // TODO: AI 服务中的返回类型。
        TokenStream res;
        // 根据是否有系统消息，选择不同的聊天方法
        if (StringUtils.isNotBlank(assistantChatParams.getSystemMessage())) {
            res = assistant.chat(assistantChatParams.getMessageId(), assistantChatParams.getSystemMessage(), assistantChatParams.getUserMessage());
        } else {
            res = assistant.chatWithoutSystemMessage(assistantChatParams.getMessageId(), assistantChatParams.getUserMessage());
        }
        QwenLLMService.registerTokenStreamCallBack(res, sseAskParams);
    }
}

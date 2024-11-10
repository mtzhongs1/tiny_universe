package com.ailu.server.service.gpt.model;

import dev.langchain4j.memory.ChatMemory;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Data
public class SseAskParams {

    private String modelName;

    private String regenerateQuestionUuid;

    private ChatMemory chatMemory;

    private SseEmitter sseEmitter;

    private double temperature;

    /**
     * 最终提交给llm的信息，必填
     */
    private AssistantChatParams assistantChatParams;
}

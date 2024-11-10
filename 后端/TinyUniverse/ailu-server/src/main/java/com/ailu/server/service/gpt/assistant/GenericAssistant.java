package com.ailu.server.service.gpt.assistant;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.service.SystemMessage;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/23 下午10:16
 */

public interface GenericAssistant {
    @SystemMessage("你是一个学习小助手,帮助用户解决学习方面的问题")
    AiMessage chat(String content);
}

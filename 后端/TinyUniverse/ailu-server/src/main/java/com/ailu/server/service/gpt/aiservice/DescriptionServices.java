package com.ailu.server.service.gpt.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @Description: 提取实体和关系的服务
 * @Author: ailu
 * @Date: 2024/10/28 下午8:29
 */

public interface DescriptionServices {
    @SystemMessage("作为一名文本分析大师，你的任务是从用户提供的文本中提取出文章的概要，要求提取出的概要严格少于100个字（一个字符或汉字为一个字）")
    @UserMessage("文本：{{prompt}}")
    String description(@V("prompt") String prompt);
}

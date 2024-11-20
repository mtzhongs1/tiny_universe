package com.ailu.server.service.gpt.aiservice;

import com.zhipu.oapi.service.v4.model.ChatMessage;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/11/19 下午1:52
 */

public interface ModifyArticleServices {
    @SystemMessage("作为一名全能作家，你的任务是根据用户提供的文章和写作风格，对文章进行润色。\\n" +
            "要求:\\n" +
            "1.润色后的文章符合用户要求的写作风格。\\n" +
            "2.文章如果包含html标签和css样式，请保持结构和样式不变。\\n" +
            "3.对方的输入格式:[[文章内容:...]]，[[写作风格:...]];你的输出格式:[[...]]。\\n" +
            "示例:\\n" +
            "用户输入:[[文章内容:<p>你好聪明啊</p>]]，[[写作风格:讽刺风格]]，润色后的文章:[[<p>哦，您真是太有智慧了，我简直要被您的聪明才智给闪瞎眼了！</p>]]")
    @UserMessage("[[文章内容:{{content}}]],[[写作风格:{{style}}]]")
    String modifyArticle(@V("content") String content,@V("style") String style);
}

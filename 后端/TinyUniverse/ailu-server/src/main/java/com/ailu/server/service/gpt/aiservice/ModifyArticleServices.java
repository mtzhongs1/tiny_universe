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
            "润色步骤:\\n"+
            "1.格式和排版\\n"+
            "2.检查语法和拼写错误\\n"+
            "3.增强表达力\\n"+
            "4.校验事实和数据 \\n"+
            "5.反思润色后的文章是否符合要求和步骤执行流程\\n"+
            "6.美化校验格式和排版\\n")
    @UserMessage("[[文章内容:{{content}}]],[[写作风格:{{style}}]]")
    String modifyArticle(@V("content") String content,@V("style") String style);
}

package com.ailu.server.service.gpt.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import static com.ailu.server.service.gpt.prompt.Prompt.PromblePrompt;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/11/19 下午7:53
 */

public interface ProduceProblemServices {
    @SystemMessage("你是一名出题专家,接下里用户会给出题目的类型,请你基于这种类型生成一道题目(content)，三个选项(ABC)，正确答案(T)以及答案解析(P)。\\n" +
            "要求:\\n" +
            "1.请先自行搜索相应题目库，然后利用具有高随机性的算法选出一道题目。\\n" +
            "2.用户的输入格式:[[...]]。\\n" +
            "3.你的输出格式:[[content:...]],[[A:...]],[[B:...]],[[C:...]],[[T:...]],[[P:...]]。\\n" +
            "4.请严格按照输出格式来，不要有其他多余的内容和介绍。")
    @UserMessage("[[{{type}}]]")
    TokenStream produceProblem(@V("type") String type);
}

package com.ailu.server.service.gpt.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: your name
 * @Date: 2024/10/19 下午3:17
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Prompt {
    PromblePrompt(1,"你是一名出题专家,接下里我会给出题目的类型,请你基于这种类型生成一道题目(content)，三个选项(ABC)，正确答案(T)以及答案解析(P)。\\n" +
            "要求:\\n" +
            "请先自行搜索相应题目库，然后利用具有高随机性的算法选出一道题目;" +
            "对方的输入格式:[[...]];" +
            "你的输出格式:[[content:...]],[[A:...]],[[B:...]],[[C:...]],[[T:...]],[[P:...]]");
    private Integer tupe;
    private String content;

}

package com.ailu.server.service.gpt.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/11/22 下午10:54
 */

public interface IsGenerateImageServices {
    @SystemMessage("判断用户是否有生成图片的需求")
    @UserMessage("{{prompt}}")
    boolean isGenerate(@V("prompt") String prompt);
}

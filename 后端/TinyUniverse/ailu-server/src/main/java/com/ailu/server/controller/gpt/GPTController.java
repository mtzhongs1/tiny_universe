package com.ailu.server.controller.gpt;

import com.ailu.dto.article.ArticleModifyDTO;
import com.ailu.entity.Problem;
import com.ailu.result.Result;
import com.ailu.server.service.LLMFactory;
import com.ailu.server.service.gpt.GPTService;
import com.zhipu.oapi.service.v4.model.ModelData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/9 下午12:30
 */

@RestController
@RequestMapping(value = "/gpt")
@Slf4j
public class GPTController {
    @Autowired
    private LLMFactory llmFactory;

    @GetMapping("/chat")
    public Result chat(String content) throws IOException{
        GPTService qianFanServiceImpl = llmFactory.getLLMService("glm");
        String chat = qianFanServiceImpl.chat(content);
        return Result.success(chat);
    }

    @GetMapping("/description")
    public Result description(Integer id,String content) throws IOException {
        GPTService qianFanServiceImpl = llmFactory.getLLMService("glm");
        String chat = qianFanServiceImpl.description(id,content);
        return Result.success(chat);
    }

    @PostMapping("/modifyArticle")
    public Result<ModelData> modifyArticle(@RequestBody ArticleModifyDTO articleModifyDTO) {
        GPTService glmServiceImpl = llmFactory.getLLMService("glm");
        ModelData result = (ModelData) glmServiceImpl.modifyArticle(articleModifyDTO);
        return Result.success(result);
    }

    //TODO:同步调用
    @GetMapping("/produceProblem")
    public Result<Problem> produceProblem(String type) {
        GPTService glmServiceImpl = llmFactory.getLLMService("glm");
        return Result.success(glmServiceImpl.produceProblem(type));
    }

    //TODO:流式输出
    @GetMapping("/sse/produceProblem")
    public SseEmitter produceProblemBySSE(String type) {
        GPTService glmServiceImpl = llmFactory.getLLMService("glm");
        return glmServiceImpl.produceProblemBySSE(type);
    }

    @GetMapping("/chatByRag")
    public SseEmitter chatByRAG(String kbUuid,String problem){
        GPTService glmServiceImpl = llmFactory.getLLMService("glm");
        return glmServiceImpl.chatByRag(problem,kbUuid);

    }

    @GetMapping("/agent")
    public Result agent(String problem,String knowledgeId){
        GPTService glmServiceImpl = llmFactory.getLLMService("glm");
        String resposne = glmServiceImpl.agent(problem,knowledgeId);
        return Result.success(resposne);
    }

}

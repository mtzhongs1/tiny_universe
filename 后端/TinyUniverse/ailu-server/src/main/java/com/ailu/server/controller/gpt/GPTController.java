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
        GPTService qwenServiceImpl = llmFactory.getLLMService("qwen");
        String chat = qwenServiceImpl.agent(content,null);
        return Result.success(chat);
    }

    @GetMapping("/description")
    public Result description(Integer id,String content) throws IOException {
        GPTService qianFanServiceImpl = llmFactory.getLLMService("qwen");
        String chat = qianFanServiceImpl.description(id,content);
        return Result.success(chat);
    }

    @PostMapping("/modifyArticle")
    public Result<String> modifyArticle(@RequestBody ArticleModifyDTO articleModifyDTO) {
        GPTService qwenServiceImpl = llmFactory.getLLMService("qwen");
        String result = qwenServiceImpl.modifyArticle(articleModifyDTO);
        return Result.success(result);
    }

    // //TODO:同步调用
    // @GetMapping("/produceProblem")
    // public Result<Problem> produceProblem(String type) {
    //     GPTService qwenServiceImpl = llmFactory.getLLMService("qwen");
    //     return Result.success(qwenServiceImpl.produceProblem(type));
    // }

    //TODO:流式输出
    @GetMapping("/sse/produceProblem")
    public SseEmitter produceProblemBySSE(String type) {
        GPTService qwenServiceImpl = llmFactory.getLLMService("qwen");
        return qwenServiceImpl.produceProblemBySSE(type);
    }

    @GetMapping("/chatByRag")
    public SseEmitter chatByRAG(String kbUuid,String problem){
        GPTService qwenServiceImpl = llmFactory.getLLMService("qwen");
        return qwenServiceImpl.chatByRag(problem,kbUuid);

    }

    @GetMapping("/agent")
    public Result agent(String problem,String knowledgeId){
        GPTService qwenServiceImpl = llmFactory.getLLMService("qwen");
        String resposne = qwenServiceImpl.agent(problem,knowledgeId);
        return Result.success(resposne);
    }

}

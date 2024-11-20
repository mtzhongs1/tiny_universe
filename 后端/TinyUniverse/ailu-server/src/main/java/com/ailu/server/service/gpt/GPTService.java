package com.ailu.server.service.gpt;

import com.ailu.dto.article.ArticleModifyDTO;
import com.ailu.entity.Problem;
import com.ailu.result.Result;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/9 下午2:08
 */

public interface GPTService {
    String chat(String content) throws IOException;
    SseEmitter chatByRag(String problem, String kbUuid);
    String description(Integer id,String content) throws IOException;
    String modifyArticle(ArticleModifyDTO articleModifyDTO);
    Problem produceProblem(String type);
    SseEmitter produceProblemBySSE(String type);

    String agent(String problem,String knowledgeId);
}

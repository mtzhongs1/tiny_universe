package com.ailu.server.service.gpt.impl;

import com.ailu.dto.article.ArticleModifyDTO;
import com.ailu.entity.Problem;
import com.ailu.result.Result;
import com.ailu.server.service.gpt.GPTService;
import com.ailu.server.util.RedisLimiterManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/19 下午2:57
 */
@Service("QianFanServiceImpl")
public class QianFanServiceImpl implements GPTService {

    private final MediaType mediaType = MediaType.parse("application/json");
    private OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @Autowired
    private RedisLimiterManager redisLimiterManager;
    @Override
    public String chat(String content) throws IOException {
        // MediaType mediaType = MediaType.parse("application/json");
        content = content.replaceAll("\\s*|\r|\n|\t\"","");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\"messages\":[{\"content\":\""+content+"\",\"role\":\"user\"}],\"temperature\":0.95,\"top_p\":0.7,\"penalty_score\":1,\"system\":\"你是一名学习助理，专门帮助学生解决疑难杂症\"}");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-tiny-8k?access_token=24.76770bd96738cf314b0f7baed0bba633.2592000.1731158562.282335-92565648")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String allRes = response.body().string();
        JSONObject jsonObject = JSON.parseObject(allRes);
        return jsonObject.getString("result");
    }

    @Override
    public SseEmitter chatByRag(String problem, String kbUuid) {
        return null;
    }

    @Override
    public String description(Integer id, String content) throws IOException {
        redisLimiterManager.doRateLimit(id.toString());
        content = "请根据["+content+"]，简短地总结上面的文章，不超过50个字";
        content = content.replaceAll("\\s*|\r|\n|\t\"","");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\"messages\":[{\"content\":\""+content+"\",\"role\":\"user\"}],\"temperature\":0.95,\"top_p\":0.7,\"penalty_score\":1,\"system\":\"你是一名概要设计师，专门负责为文章生成一篇不超过50字的总结\"}");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-tiny-8k?access_token=24.76770bd96738cf314b0f7baed0bba633.2592000.1731158562.282335-92565648")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String allRes = response.body().string();
        JSONObject jsonObject = JSON.parseObject(allRes);
        return jsonObject.getString("result");
    }

    @Override
    public String modifyArticle(ArticleModifyDTO articleModifyDTO) {
        return "";
    }

    @Override
    public Problem produceProblem(String type) {
        return null;
    }

    @Override
    public SseEmitter produceProblemBySSE(String type) {
        return null;
    }

    @Override
    public String agent(String problem,String knowledgeId) {
        return null;
    }
}

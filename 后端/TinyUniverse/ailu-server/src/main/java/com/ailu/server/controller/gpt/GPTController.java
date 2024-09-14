package com.ailu.server.controller.gpt;

import com.ailu.result.Result;
// import com.ailu.server.util.RedisLimiterManager;
import com.ailu.server.util.RedisLimiterManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @Autowired
    private RedisLimiterManager redisLimiterManager;

    private final MediaType mediaType = MediaType.parse("application/json");

    @GetMapping("/description")
    public Result description(Integer id,String content) throws IOException {
        redisLimiterManager.doRateLimit(id.toString());
        content = "请根据["+content+"]，简短地总结上面的文章，不超过50个字";
        content = content.replaceAll("\\s*|\r|\n|\t\"","");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\"messages\":[{\"content\":\""+content+"\",\"role\":\"user\"}],\"temperature\":0.95,\"top_p\":0.7,\"penalty_score\":1,\"system\":\"你是一名概要设计师，专门负责为文章生成一篇不超过50字的总结\"}");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-tiny-8k?access_token=24.c2ba2934bc2d57552f29dd990219a2ba.2592000.1727767739.282335-92565648")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String allRes = response.body().string();
        JSONObject jsonObject = JSON.parseObject(allRes);
        return Result.success(jsonObject.getString("result"));
    }

    @GetMapping("/chat")
    public Result chat(String content) throws IOException{
        // MediaType mediaType = MediaType.parse("application/json");
        content = content.replaceAll("\\s*|\r|\n|\t\"","");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\"messages\":[{\"content\":\""+content+"\",\"role\":\"user\"}],\"temperature\":0.95,\"top_p\":0.7,\"penalty_score\":1,\"system\":\"你是一名学习助理，专门帮助学生解决疑难杂症\"}");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-tiny-8k?access_token=24.c2ba2934bc2d57552f29dd990219a2ba.2592000.1727767739.282335-92565648")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String allRes = response.body().string();
        JSONObject jsonObject = JSON.parseObject(allRes);
        return Result.success(jsonObject.getString("result"));
    }

    @GetMapping("/article_modify")
    public Result articleModify(String content,String type,String description) throws IOException{
        return null;
    }

}

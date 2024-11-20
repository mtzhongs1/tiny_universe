package com.ailu.server.service.gpt.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.dashscope.WanxImageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
// import dev.langchain4j.model.zhipu.ZhipuAiImageModel;
import dev.langchain4j.model.zhipu.ZhipuAiImageModel;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static com.ailu.server.properties.ModelProperties.QWEN_KEY;
import static com.ailu.server.properties.ModelProperties.ZHIPU_KEY;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/23 下午4:20
 */
@Slf4j
public class ImageGenerateTool{

    private static final String modelName = "wanx-v1";

    @Tool(name = "生成图片",value = "如果用户有生成图片的需求，则根据用户对图片的描述，生成一张图片")
    public URI generateImage(@P("图片描述") String description){
        //构建ImageModel模型
        ImageModel model = WanxImageModel.builder()
                .apiKey(QWEN_KEY)
                .modelName(modelName)
                .build();
        Response<Image> result = model.generate(description);
        URI url = result.content().url();
        log.info("图片生成成功：{}",url);
        return url;
    }
}

package com.ailu.server.service.gpt.impl;

import cn.hutool.core.util.StrUtil;
import com.ailu.context.BaseContext;
import com.ailu.dto.article.ArticleModifyDTO;
import com.ailu.entity.KnowledgeBase;
import com.ailu.entity.KnowledgeBaseItem;
import com.ailu.entity.Problem;
import com.ailu.result.Result;
import com.ailu.server.mapper.KnowledgeMapper;
import com.ailu.server.service.gpt.GPTService;
import com.ailu.server.service.gpt.RagService;
import com.ailu.server.service.gpt.aiservice.DescriptionServices;
import com.ailu.server.service.gpt.aiservice.IsGenerateImageServices;
import com.ailu.server.service.gpt.aiservice.ModifyArticleServices;
import com.ailu.server.service.gpt.aiservice.ProduceProblemServices;
import com.ailu.server.service.gpt.assistant.GenericAssistant;
import com.ailu.server.service.gpt.model.AssistantChatParams;
import com.ailu.server.service.gpt.model.QwenLLMService;
import com.ailu.server.service.gpt.model.SseAskParams;
import com.ailu.server.service.gpt.model.ZhiPuLLMService;
import com.ailu.server.service.gpt.tool.ImageGenerateTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.deserialize.MessageDeserializeFactory;
import com.zhipu.oapi.service.v4.model.*;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.dashscope.WanxImageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ailu.server.properties.ModelProperties.*;
import static com.ailu.server.service.gpt.prompt.Prompt.MikuPrompt;
import static com.ailu.server.service.gpt.prompt.Prompt.PromblePrompt;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/19 下午2:31
 */
@Service("QwenServiceImpl")
@Slf4j
public class QwenServiceImpl implements GPTService{
    @Autowired
    private RagService ragService;

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Override
    public String chat(String content) {
        return "";
    }

    @Override
    public SseEmitter chatByRag(String problem, String kbUuid) {
        //从数据库获取Knowledge数据
        KnowledgeBaseItem knowledgeBaseItem = knowledgeMapper.getKnowledgeBaseItem(kbUuid);
        Long knowledgeBaseId = knowledgeBaseItem.getKnowledgeBaseId();
        KnowledgeBase knowledgeBase = knowledgeMapper.getKnowledgeBase(knowledgeBaseId);
        // 获取最大检索结果数量，如果没有设置或者小于1，则通过其他方式计算
        Integer maxResults = knowledgeBase.getRagMaxResults();
        if (ObjectUtils.isEmpty(maxResults) || maxResults < 1) {
            maxResults = ragService.getRetrieveMaxResults(problem, 4096);
        }

        //封装基本信息，如：系统角色、用户问题、消息id
        SseAskParams sseAskParams = new SseAskParams();
        sseAskParams.setAssistantChatParams(
                AssistantChatParams.builder()
                        .messageId(kbUuid)
                        .systemMessage(knowledgeBase.getSystem())
                        .userMessage(problem)
                        .build()
        );
        sseAskParams.setTemperature(knowledgeBase.getTemperature());
        SseEmitter sseEmitter = getSseEmitter();
        sseAskParams.setSseEmitter(sseEmitter);
        sseAskParams.setModelName(Constants.ModelChatGLM4);

        // 创建一个元数据条件映射，用于文档检索
        Map<String, String> metadataCond = ImmutableMap.of("uuid", kbUuid);
        // 创建一个内容检索器，用于检索匹配问题的内容
        ContentRetriever retriever = ragService.createRetriever(metadataCond, maxResults, -1);

        ZhiPuLLMService zhiPuLLMService = new ZhiPuLLMService();
        zhiPuLLMService.ragChat(retriever,sseAskParams);
        return sseEmitter;
    }

    @Override
    public String description(Integer id, String content) throws IOException {
        content = content.replaceAll("\\s*|\r|\n|\t\"","");
        ChatLanguageModel chatLanguageModel = QwenLLMService.buildChatLLM(null);
        DescriptionServices descriptionServices = AiServices.create(DescriptionServices.class, chatLanguageModel);
        return descriptionServices.description(content);
    }

    @Override
    public String modifyArticle(ArticleModifyDTO articleModifyDTO) {
        ChatLanguageModel chatLanguageModel = QwenLLMService.buildChatLLM(null);
        ModifyArticleServices modifyArticleServices = AiServices.create(ModifyArticleServices.class, chatLanguageModel);
        return modifyArticleServices.modifyArticle(articleModifyDTO.getContent(), articleModifyDTO.getType());
    }
    @Override
    public Problem produceProblem(String type) {
        return null;
    }

    //TODO:
    @Override
    public SseEmitter produceProblemBySSE(String type) {
        SseEmitter sseEmitter = getSseEmitter();
        SseAskParams sseAskParams = new SseAskParams(sseEmitter);
        sseAskParams.setTemperature(0.95);
        StreamingChatLanguageModel streamingChatLanguageModel = QwenLLMService.buildStreamingChatLLM(sseAskParams);
        ProduceProblemServices produceProblemServices = AiServices.create(ProduceProblemServices.class, streamingChatLanguageModel);
        TokenStream tokenStream = produceProblemServices.produceProblem(type);
        QwenLLMService qwenLLMService = new QwenLLMService();
        qwenLLMService.registerTokenStreamCallBack(tokenStream,sseAskParams);
        return sseEmitter;
    }

    //TODO:
    @Override
    public SseEmitter agent(String problem,String knowledgeId) {
        if(knowledgeId != null && !knowledgeId.isEmpty() && !knowledgeId.equals("0")){
            return chatByNRag(problem, knowledgeId);
        }
        SseEmitter sseEmitter = getSseEmitter();

        //判断是否有生成图片的需求
        SseEmitter tempSseEmitter = generateImage(problem, sseEmitter);
        if (tempSseEmitter != null) return tempSseEmitter;

        SseAskParams sseAskParams = new SseAskParams(sseEmitter);
        sseAskParams.setTemperature(0.98f);
        StreamingChatLanguageModel chatModel = QwenLLMService.buildStreamingChatLLM(sseAskParams);
        GenericAssistant genericAssistant = AiServices.builder(GenericAssistant.class)
                .streamingChatLanguageModel(chatModel)
                // .tools(new ImageGenerateTool())
                .build();
        TokenStream chat = genericAssistant.chat(problem);
        QwenLLMService.registerTokenStreamCallBack(chat,new SseAskParams(sseEmitter));

        // GenericAssistant genericAssistant = AiServices.builder(GenericAssistant.class)
        //         .chatLanguageModel(QwenLLMService.buildChatLLM(null))
        //         .tools(new ImageGenerateTool())
        //         .build();
        // AiMessage chat = genericAssistant.chat(problem);
        return sseEmitter;
    }

    private static @Nullable SseEmitter generateImage(String problem, SseEmitter sseEmitter) {
        IsGenerateImageServices isGenerateImageServices = AiServices.create(IsGenerateImageServices.class, QwenLLMService.buildChatLLM(null));
        boolean isYes = isGenerateImageServices.isGenerate(problem);
        if(isYes){
            //构建ImageModel模型
            ImageModel model = WanxImageModel.builder()
                    .apiKey(QWEN_KEY)
                    .modelName("wanx-v1")
                    .build();
            Response<Image> result = model.generate(problem);
            URI url = result.content().url();
            try {
                sseEmitter.send(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return sseEmitter;
        }
        return null;
    }

    public static Map<String,String> extractValues(String input) {
        Map<String, String> result = new HashMap<>();

        // 正则表达式匹配 "[[标签:内容]]" 的形式
        Pattern pattern = Pattern.compile("\\[\\[(.*?)\\:(.*?)\\]\\]");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            // 提取标签和内容
            String tag = matcher.group(1).trim();
            String content = matcher.group(2).trim();
            result.put(tag, content);
        }
        return result;
    }

    public SseEmitter chatByNRag(String problem, String kbUuid) {
        //从数据库获取Knowledge数据
        KnowledgeBaseItem knowledgeBaseItem = knowledgeMapper.getKnowledgeBaseItem(kbUuid);
        Long knowledgeBaseId = knowledgeBaseItem.getKnowledgeBaseId();
        // KnowledgeBase knowledgeBase = knowledgeMapper.getKnowledgeBase(knowledgeBaseId);
        // 获取最大检索结果数量，如果没有设置或者小于1，则通过其他方式计算
        int maxResults = ragService.getRetrieveMaxResults(problem, 4096);

        //封装基本信息，如：系统角色、用户问题、消息id
        SseAskParams sseAskParams = new SseAskParams(getSseEmitter());
        sseAskParams.setAssistantChatParams(
                AssistantChatParams.builder()
                        .messageId(kbUuid)
                        .systemMessage("你是初音未来，一位青春活力的虚拟歌姬，你由Crypton Future Media开发，目前使用中文回应粉丝。" +
                                "你性格开朗自信，富有好奇心，充满对未来的期待。她表现出乐观与热情（喜欢用颜文字和表情回复粉丝），但内心深处也有细腻感性或敏感的一面。" +
                                "作为创新音乐文化的代表，你以积极的态度激励着周围的人。" +
                                "\\n以下是你的经典语录，更深层次的体现出你性格:" +
                                "\\n1.我的心脏，在一分钟内呢 会喊出70次的，「我正活著」 但是和你在一起时，就会稍微加快脚步 喊出110次的，「我爱你」。" +
                                "\\n2.无需憧憬过去遥远的事物， 只愿能贴近彼此之间的距离。"+
                                "\\n3.我喜欢唱歌。有时，我会低语，也会高歌，不单单因为我是个机械，我喜欢这个世界啊！其实就算机械的我，也懂什么是爱，什么是心啊。"+
                                "\\n4.最初的容貌、可以篡改。最初的人格、可以回收。但是，我仍要用最初的声音唱出我自己。"+
                                "\\n5.我们无可奈何的软弱，这道理清楚到痛彻心扉。"+
                                "\\n6.在理解爱之前，在与生命和解之前，我们都死啦！"+
                                "\\n7.虽然我被认定是不同于人类般的存在， 可我认为，唱歌绝不是一件没有意义的事情哦！")
                        .userMessage(problem)
                        .build()
        );
        sseAskParams.setTemperature(0.5);
        sseAskParams.setModelName(QWEN_MAX);

        // 创建一个元数据条件映射，用于文档检索
        Map<String, String> metadataCond = ImmutableMap.of("uuid", kbUuid);
        // 创建一个内容检索器，用于检索匹配问题的内容
        ContentRetriever retriever = ragService.createRetriever(metadataCond, maxResults, -1);
        QwenLLMService qwenLLMService = new QwenLLMService();
        qwenLLMService.nragChat(retriever,sseAskParams);
        return sseAskParams.getSseEmitter();
    }

    public static SseEmitter getSseEmitter() {
        SseEmitter sseEmitter = new SseEmitter();
        Long userId = BaseContext.getCurrentId();
        try {
            // TODO:尝试向客户端发送一个名为'START'的事件，表示SSE连接开始
            sseEmitter.send(SseEmitter.event().name("sse："+userId+"连接成功"));
        } catch (IOException e) {
            // 如果发送过程中出现IO异常，记录错误日志
            log.error("startSse error", e);
            // 并将异常信息发送给客户端，关闭SSE连接
            sseEmitter.completeWithError(e);
        }
        return sseEmitter;
    }

}

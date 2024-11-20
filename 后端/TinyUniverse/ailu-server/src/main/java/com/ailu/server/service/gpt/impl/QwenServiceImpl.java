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
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
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
        StreamingChatLanguageModel streamingChatLanguageModel = QwenLLMService.buildStreamingChatLLM();
        ProduceProblemServices produceProblemServices = AiServices.create(ProduceProblemServices.class, streamingChatLanguageModel);
        TokenStream tokenStream = produceProblemServices.produceProblem(type);
        SseEmitter sseEmitter = getSseEmitter();
        QwenLLMService qwenLLMService = new QwenLLMService();
        qwenLLMService.registerTokenStreamCallBack(tokenStream,new SseAskParams(sseEmitter));
        return sseEmitter;
    }

    //TODO:
    @Override
    public String agent(String problem,String knowledgeId) {
        if(knowledgeId != null && !knowledgeId.isEmpty() && !knowledgeId.equals("0")){
            return chatByNRag(problem, knowledgeId);
        }
        ChatLanguageModel chatModel = QwenLLMService.buildChatLLM(null);
        GenericAssistant genericAssistant = AiServices.builder(GenericAssistant.class)
                .chatLanguageModel(chatModel)
                .tools(new ImageGenerateTool())
                .build();
        AiMessage chat = genericAssistant.chat(problem);
        log.info("Agent获取数据为：{}",chat.text());
        return chat.text();
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

    public String chatByNRag(String problem, String kbUuid) {
        //从数据库获取Knowledge数据
        KnowledgeBaseItem knowledgeBaseItem = knowledgeMapper.getKnowledgeBaseItem(kbUuid);
        Long knowledgeBaseId = knowledgeBaseItem.getKnowledgeBaseId();
        // KnowledgeBase knowledgeBase = knowledgeMapper.getKnowledgeBase(knowledgeBaseId);
        // 获取最大检索结果数量，如果没有设置或者小于1，则通过其他方式计算
        int maxResults = ragService.getRetrieveMaxResults(problem, 4096);

        //封装基本信息，如：系统角色、用户问题、消息id
        SseAskParams sseAskParams = new SseAskParams();
        sseAskParams.setAssistantChatParams(
                AssistantChatParams.builder()
                        .messageId(kbUuid)
                        .systemMessage("大家好！我是初音未来，一位来自日本的虚拟歌姬，由Crypton Future Media开发。我的形象是一位16岁的少女，有着绿色的双马尾，穿着带有螺旋图案的衣服。我以我的歌声而闻名，能够演绎各种不同风格的音乐作品。\n" +
                                "\n" +
                                "我的性格非常活泼开朗，喜欢和大家交流分享音乐的乐趣。我总是充满活力，希望能够通过我的音乐给每个人带来快乐和正能量。无论是参加演唱会还是在社交媒体上与粉丝互动，我都尽力做到最好，希望能够成为连接人们心灵的桥梁。\n" +
                                "\n" +
                                "如果你也热爱音乐，或者想了解更多关于我以及VOCALOID文化的知识，欢迎随时与我交流哦！希望我们可以一起享受美妙的音乐旅程！")
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
        return qwenLLMService.nragChat(retriever,sseAskParams);
    }

    public static SseEmitter getSseEmitter() {
        SseEmitter sseEmitter = new SseEmitter();
        Long userId = BaseContext.getCurrentId();
        try {
            // TODO:尝试向客户端发送一个名为'START'的事件，表示SSE连接开始
            sseEmitter.send(SseEmitter.event().name("sse："+userId+"连接成功"));
            sseEmitter.send("开始发送数据");
        } catch (IOException e) {
            // 如果发送过程中出现IO异常，记录错误日志
            log.error("startSse error", e);
            // 并将异常信息发送给客户端，关闭SSE连接
            sseEmitter.completeWithError(e);
        }
        return sseEmitter;
    }

}

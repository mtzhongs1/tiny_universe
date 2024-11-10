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
import com.ailu.server.service.gpt.assistant.GenericAssistant;
import com.ailu.server.service.gpt.model.AssistantChatParams;
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
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
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

import static com.ailu.server.properties.ModelProperties.GLM_4_FLASH;
import static com.ailu.server.properties.ModelProperties.ZHIPU_KEY;
import static com.ailu.server.service.gpt.prompt.Prompt.PromblePrompt;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/19 下午2:31
 */
@Service("GlmServiceImpl")
@Slf4j
public class GlmServiceImpl implements GPTService{
    @Autowired
    private RagService ragService;

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Value("${gpt.glm.apiKey}")
    @Getter
    private String API_SECRET_KEY;

    private static final ObjectMapper mapper = MessageDeserializeFactory.defaultObjectMapper();
    private static String requestIdTemplate = "ailu-tiny_universe";
    private ClientV4 client;

    @PostConstruct
    public void init() {
        client = new ClientV4.Builder(this.API_SECRET_KEY)
                .networkConfig(300, 100, 100, 100, TimeUnit.SECONDS)
                .connectionPool(new okhttp3.ConnectionPool(8, 1, TimeUnit.SECONDS))
                .build();
    }

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
        return "";
    }

    @Override
    public ModelData modifyArticle(ArticleModifyDTO articleModifyDTO) {
        List<ChatMessage> list = getChatMessages(articleModifyDTO);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(list)
                .requestId(requestIdTemplate +System.currentTimeMillis())
                .toolChoice("auto")
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        try {
            System.out.println("model output:" + mapper.writeValueAsString(invokeModelApiResp));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return invokeModelApiResp.getData();
    }
    @Override
    public Problem produceProblem(String type) {
        ChatMessage msg1 = new ChatMessage(ChatMessageRole.SYSTEM.value(),PromblePrompt.getContent());
        ChatMessage msg5 = new ChatMessage(ChatMessageRole.USER.value(), "[["+type+"]]");
        List<ChatMessage> list = Arrays.asList(msg1, msg5);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(list)
                .requestId(requestIdTemplate)
                .toolChoice("auto")
                .temperature(1.0f)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        String s = invokeModelApiResp.getData().getChoices().get(0).get("message").get("content").asText();
        Map<String, String> map = extractValues(s);
        return new Problem(map.get("content"), map.get("A"), map.get("B"), map.get("C"), map.get("T"),map.get("P"));
    }
    @Override
    public SseEmitter produceProblemBySSE(String type) {
        ChatMessage msg1 = new ChatMessage(ChatMessageRole.SYSTEM.value(),PromblePrompt.getContent());
        ChatMessage msg5 = new ChatMessage(ChatMessageRole.USER.value(), "[["+type+"]]");
        List<ChatMessage> list = Arrays.asList(msg1, msg5);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .invokeMethod(Constants.invokeMethod)
                .messages(list)
                .requestId(requestIdTemplate)
                // .tools(Arrays.asList(new ChatTool()))
                .toolChoice("auto")
                .temperature(1.0f)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        Flowable<ModelData> flowable = invokeModelApiResp.getFlowable();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        Scheduler scheduler = Schedulers.from(scheduledExecutorService);
        // 左括号计数器，除了默认值外，当回归为 0 时，表示左括号等于右括号，可以截取
        AtomicInteger counter = new AtomicInteger(0);
        StringBuilder stringBuilder = new StringBuilder();
        // TODO:建立 SSE 连接对象，0 表示永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        flowable.observeOn(scheduler)
                .map(modelData -> modelData.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    List<Character> characterList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        characterList.add(c);
                    }
                    return Flowable.fromIterable(characterList);
                })
                .doOnNext(c -> {
                    if (c == '[') {
                        counter.addAndGet(1);
                    }
                    else if (c == ']') {
                        counter.addAndGet(-1);
                        if (counter.get() == 0) {
                            // TODO:通过 SSE 返回给前端
                            sseEmitter.send(stringBuilder.toString());
                            log.info("send: " + stringBuilder);
                            // 重置，准备拼接下一道题
                            stringBuilder.setLength(0);
                        }
                    }
                    else if (c == ',') {
                        // 逗号，不输出，只做计数用
                    }
                    else{
                        log.info(stringBuilder.toString());
                        stringBuilder.append(c);
                    }
                })
                .doOnError((e) -> log.error("sse error", e))
                //TODO:关闭连接
                .doOnComplete(sseEmitter::complete)
                //交给线程池执行任务
                .subscribe();
        return sseEmitter;
    }

    @Override
    public String agent(String problem,String knowledgeId) {
        if(knowledgeId != null && !knowledgeId.isEmpty() && !knowledgeId.equals("0")){
            return chatByNRag(problem, knowledgeId);
        }
        ChatLanguageModel chatModel = ZhipuAiChatModel.builder()
                .apiKey(ZHIPU_KEY)
                .model(GLM_4_FLASH)
                .callTimeout(Duration.of(20000, ChronoUnit.MILLIS))
                .connectTimeout(Duration.of(20000, ChronoUnit.MILLIS))
                .writeTimeout(Duration.of(20000, ChronoUnit.MILLIS))
                .readTimeout(Duration.of(20000, ChronoUnit.MILLIS))
                .build();
        GenericAssistant genericAssistant = AiServices.builder(GenericAssistant.class)
                .chatLanguageModel(chatModel)
                .tools(new ImageGenerateTool())
                .build();
        AiMessage chat = genericAssistant.chat(problem);
        log.info("Agent获取数据为：{}",chat.text());
        return chat.text();
    }

    private static @NotNull List<ChatMessage> getChatMessages(ArticleModifyDTO articleModifyDTO) {
        ChatMessage msg1 = new ChatMessage("user","你是一名全能作家,接下里我会给出我的文章内容和一种写作风格,请你基于这种写作风格为我的文章进行润色。\\n" +
                "要求:\\n" +
                "文章包含html标签和css样式，请保持结构和样式不变" +
                "对方的输入格式:[[文章内容:...]]，[[写作风格:...]]" +
                "你的输出格式:[[...]]");
        ChatMessage msg2 = new ChatMessage("assistant", "当然，请你给出你的文章内容和写作风格，我会进行润色");
        ChatMessage msg3 = new ChatMessage("user", "[[文章内容:<p>你好聪明啊</p>]]，[[写作风格:讽刺风格]]");
        ChatMessage msg4 = new ChatMessage("assistant", "[[你可真是大聪明啊!]]");
        ChatMessage msg5 = new ChatMessage("user", "[[文章内容:"+ articleModifyDTO.getContent()+"]]，[[写作风格:"+ articleModifyDTO.getType()+"]]");
        return Arrays.asList(msg1, msg2, msg3, msg4, msg5);
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
                        .systemMessage("你是一个学习助理，帮助学生结局问题")
                        .userMessage(problem)
                        .build()
        );
        sseAskParams.setTemperature(0.7);
        sseAskParams.setModelName(Constants.ModelChatGLM4);

        // 创建一个元数据条件映射，用于文档检索
        Map<String, String> metadataCond = ImmutableMap.of("uuid", kbUuid);
        // 创建一个内容检索器，用于检索匹配问题的内容
        ContentRetriever retriever = ragService.createRetriever(metadataCond, maxResults, -1);

        ZhiPuLLMService zhiPuLLMService = new ZhiPuLLMService();
        return zhiPuLLMService.nragChat(retriever,sseAskParams);
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

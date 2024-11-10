package com.ailu.server.service;

import com.ailu.server.properties.LLMSourceProperties;
import com.ailu.server.service.gpt.GPTService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/19 下午2:38
 */
@Component
public class LLMFactory implements ApplicationContextAware {
    private static final Map<String, GPTService> map = new ConcurrentHashMap<>();

    @Autowired
    private LLMSourceProperties llmSourceProperties;

    public GPTService getLLMService(String llmType) {
        return map.get(llmType);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        Map<String, String> llmTypes = llmSourceProperties.getLlm();
        llmTypes.forEach((k,v) -> {
            GPTService bean = applicationContext.getBean(v, GPTService.class);
            map.put(k, bean);
        });
    }
}

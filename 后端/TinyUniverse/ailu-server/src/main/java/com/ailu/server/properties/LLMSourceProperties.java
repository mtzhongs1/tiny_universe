package com.ailu.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/19 下午2:48
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "gpt")
public class LLMSourceProperties {
    private Map<String,String> llm;
}

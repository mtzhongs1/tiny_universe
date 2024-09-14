package com.ailu.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/9 下午8:39
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "ailu")
public class DataSourceProperties {
    private Map<String,String> searchTypes;
}

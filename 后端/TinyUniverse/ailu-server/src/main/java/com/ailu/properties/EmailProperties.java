package com.ailu.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午5:15
 */

@ConfigurationProperties(prefix = "spring.mail.properties")
@Data
public class EmailProperties {
    private String from;
}

package com.ailu.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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

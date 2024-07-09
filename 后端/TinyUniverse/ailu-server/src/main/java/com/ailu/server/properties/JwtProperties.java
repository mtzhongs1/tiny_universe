package com.ailu.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午9:52
 */
@Data
@ConfigurationProperties(prefix = "ailu.jwt")
public class JwtProperties {
    private String secret;
    private String expiration;
}

package com.ailu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 上午12:24
 */

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@ConfigurationPropertiesScan("com.ailu.properties")
@MapperScan(basePackages = "com.ailu.mapper")
public class AiluServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiluServerApplication.class, args);
    }
}

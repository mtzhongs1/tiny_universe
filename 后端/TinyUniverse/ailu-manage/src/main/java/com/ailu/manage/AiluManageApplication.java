package com.ailu.manage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/8 上午11:22
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@ConfigurationPropertiesScan
@MapperScan(basePackages = "com.ailu.manage.mapper")
public class AiluManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiluManageApplication.class, args);
    }
}

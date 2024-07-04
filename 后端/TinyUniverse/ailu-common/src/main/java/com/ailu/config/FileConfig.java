package com.ailu.config;

import org.apache.tomcat.util.http.fileupload.impl.FileItemStreamImpl;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import sun.tools.jar.resources.jar;

import javax.servlet.MultipartConfigElement;

/**
 * @Description:
 * @Author: ailu
 * @Date: 11/6/2024 下午4:15
 */

@Configuration
public class FileConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //允许上传的文件最大值
        factory.setMaxFileSize(DataSize.parse("10MB")); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.parse("50MB"));
        return factory.createMultipartConfig();
    }
}

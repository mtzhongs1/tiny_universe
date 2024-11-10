package com.ailu.server.config.system;

import com.ailu.server.filter.JwtAuthenticationTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: ailu
 * @Date: 3/6/2024 上午11:59
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<JwtAuthenticationTokenFilter> jwtAuthenticationTokenFilter(){
        FilterRegistrationBean<JwtAuthenticationTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtAuthenticationTokenFilterBean());
        registrationBean.addUrlPatterns("/*");
        //排除
        registrationBean.addInitParameter("excludedUris","/email/sendMailCode,/user/login,/user/register,/ws/*,/doc.html,/swagger-resources,/swagger-ui.html,/webjars/**," +
                "/swagger-resources/**,/v2/**,/ws/**," +
                "/gpt/sse/produceProblem/**,/gpt/rag/produceProblem/**");
        return registrationBean;
    }

    @Bean
    // 让过滤器能注入Bean对象
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilterBean(){
        return new JwtAuthenticationTokenFilter();
    }
}

package com.ailu.filter;

import com.ailu.context.BaseContext;
import com.ailu.properties.JwtProperties;
import com.ailu.result.Result;
import com.ailu.util.JwtUtil;
import com.ailu.util.WebUtils;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午10:46
 */

@Slf4j
@Component
public class JwtAuthenticationTokenFilter implements Filter{

    //TODO:Filter的加载优于spring容器初始化，所以返回null
    @Autowired
    private JwtProperties jwtProperties;

    private String[] excludedUris;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig){
        String param = filterConfig.getInitParameter("excludedUris");
        if (StringUtils.isNotBlank(param)) {
            this.excludedUris = param.split(",");
        }
    }
    @Override
    public void doFilter(ServletRequest request1, ServletResponse response1, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) request1;
        HttpServletResponse response = (HttpServletResponse) response1;

        //拦截器取到请求先进行判断，如果是OPTIONS请求，则放行
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request,response);
            return;
        }

        String uri = request.getRequestURI();
        for(String excludedUri : excludedUris){
            boolean isMatch = pathMatcher.match(excludedUri, uri);
            if(isMatch){
                filterChain.doFilter(request,response);
                return;
            }
        }

        String token = request.getHeader("token");
        token = StringUtils.isEmpty(token) ? request.getParameter("token") : token;
        if(StringUtils.isEmpty(token)){
            WebUtils.renderString(response, Result.errorToken("未登录"));
            return;
        }
        Claims claims;
        try{
            claims = JwtUtil.parseJWT(jwtProperties.getSecret(),token);
        }catch(Exception e){
            log.error("token解析失败");
            WebUtils.renderString(response,Result.errorToken("token解析失败"));
            return;
        }
        String userId = claims.get("userId").toString();
        BaseContext.setCurrentId(Long.valueOf(userId));
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        BaseContext.removeCurrentId();
    }
}

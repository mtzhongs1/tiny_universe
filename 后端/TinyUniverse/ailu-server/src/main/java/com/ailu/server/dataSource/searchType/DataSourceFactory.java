package com.ailu.server.dataSource.searchType;

import com.ailu.server.properties.DataSourceProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/8 下午5:44
 */

//TODO:简单工厂+享元模式+读取配置文件+策略模式
@Component
public class DataSourceFactory implements ApplicationContextAware {
    private final Map<String, DataSource> map = new ConcurrentHashMap<>();

    @Autowired
    private DataSourceProperties dataSourceProperties;

    public DataSource getDataSource(String type) {
        return map.get(type);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        Map<String, String> searchTypes = dataSourceProperties.getSearchTypes();
        searchTypes.forEach((k,v) -> {
            map.put(k, applicationContext.getBean(v, DataSource.class));
        });
    }
}

package com.ailu.server.config.spring;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 下午8:50
 */

//TODO:将Spring中所有的Bean静态化到上下文类中
@Component
public class SpringBeanContext implements ApplicationContextAware {

    /**
     * -- GETTER --
     *  获取上下文
     *
     * @return 上下文对象
     */
    @Getter
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 根据beanName获取bean
     *
     * @param beanName bean名称
     * @return bean对象
     */
    public Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     * 根据beanName和类型获取bean
     *
     * @param beanName bean名称
     * @param clazz    bean类型
     * @param <T>      bean类型
     * @return bean对象
     */
    public <T> T getBean(String beanName, Class<T> clazz) {
        return context.getBean(beanName, clazz);
    }

    /**
     * 根据类型获取bean
     *
     * @param clazz bean类型
     * @param <T>   bean类型
     * @return bean对象
     */
    public <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}

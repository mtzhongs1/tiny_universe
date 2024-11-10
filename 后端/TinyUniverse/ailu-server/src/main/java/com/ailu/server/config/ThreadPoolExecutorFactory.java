package com.ailu.server.config;

import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/7 下午11:18
 */

@Configuration
public class ThreadPoolExecutorFactory {

    // @Bean
    // public ThreadPoolExecutor freeThreadPoolExecutor(){
    //     //线程工厂
    //     ThreadFactory threadFactory = new ThreadFactory() {
    //         @Override
    //         public Thread newThread(@NotNull Runnable r) {
    //             Thread thread = new Thread(r);
    //             // 非守护线程:确保任务不会因为JVM决定退出而导致任务中断
    //             thread.setDaemon(false);
    //             return thread;
    //         }
    //     };
    //     return new ThreadPoolExecutor(1,2,100, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(4),threadFactory);
    // }

    // @Bean ThreadPoolExecutor vipThreadPoolExecutor() {
    //     //线程工厂
    //     ThreadFactory threadFactory = new ThreadFactory() {
    //         @Override
    //         public Thread newThread(@NotNull Runnable r) {
    //             return new Thread(r);
    //         }
    //     };
    //     return new ThreadPoolExecutor(2,4,100, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(4),threadFactory);
    // }
}

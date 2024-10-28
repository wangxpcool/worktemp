package com.kmerit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    // 创建线程池执行任务
    @Bean
    ThreadPoolExecutor getThreadPool(){
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(8, 10, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));
        return threadPool;
    }


}

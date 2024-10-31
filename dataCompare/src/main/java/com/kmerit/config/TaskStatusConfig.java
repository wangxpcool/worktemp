package com.kmerit.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class TaskStatusConfig {

    @Bean
    public ConcurrentHashMap<String, Boolean> taskStatus() {
        return new ConcurrentHashMap<>();
    }
}

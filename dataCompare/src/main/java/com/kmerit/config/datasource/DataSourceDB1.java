package com.kmerit.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "spring.datasource.druid1")
public class DataSourceDB1 extends DruidDataSource {
    // 留空，通过@ConfigurationProperties自动绑定配置
}


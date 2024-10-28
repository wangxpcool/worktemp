package com.kmerit.config.datasource;




import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DruidConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid1")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid2")
    public DataSource druidDataSource2() {
        return new DruidDataSource();
    }
}
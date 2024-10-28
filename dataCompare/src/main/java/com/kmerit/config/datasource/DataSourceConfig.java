package com.kmerit.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
        import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "dataSourceDB1")
    @ConfigurationProperties(prefix = "spring.datasource.druid1")
    public DataSource dataSourceDB1() {
        return new DruidDataSource();
    }

    @Bean(name = "dataSourceDB2")
    @ConfigurationProperties(prefix = "spring.datasource.druid2")
    public DataSource dataSourceDB2() {
        return new DruidDataSource();
    }
}

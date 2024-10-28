package com.kmerit.config.datasource;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.kmerit.mapper.datasouce1"}, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfig {

    @Primary
    @Bean(name = "dataSource1")
    @ConfigurationProperties("spring.datasource1")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "masterTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("dataSource1") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource());
    }

    @Primary
    @Bean(name = "masterSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource1") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/master/*.xml"));
        return factoryBean.getObject();
    }

    @Primary
    @Bean(name = "masterSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

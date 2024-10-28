package com.kmerit;


import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class SpringBootDataCompareApplication implements ApplicationContextAware {


    ApplicationContext applicationContext;

    public static void main(String[] args) {

        SpringApplication.run(SpringBootDataCompareApplication.class, args);

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

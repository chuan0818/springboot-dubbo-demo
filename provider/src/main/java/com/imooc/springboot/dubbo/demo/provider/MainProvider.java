package com.imooc.springboot.dubbo.demo.provider;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class MainProvider {

    public static void main(String[] args) {

        SpringApplication.run(MainProvider.class,args);

    }

}
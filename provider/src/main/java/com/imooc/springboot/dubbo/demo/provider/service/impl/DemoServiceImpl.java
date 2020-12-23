package com.imooc.springboot.dubbo.demo.provider.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.imooc.springboot.dubbo.demo.provider.service.DemoService;

@Service
public class DemoServiceImpl implements DemoService {

    public String sayHello(String name) {
        return "Hello, " + name + " (from Spring Boot)";
    }

}
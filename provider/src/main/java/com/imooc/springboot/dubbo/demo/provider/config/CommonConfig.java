package com.imooc.springboot.dubbo.demo.provider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 通用配置类
 */
@Component
public class CommonConfig {
    @Value("${xyydubbo.filterContainInvokeMethodStr:}")
    private String filterContainInvokeMethodStr;


    public String getFilterContainInvokeMethodStr() {
        return filterContainInvokeMethodStr;
    }

    public void setFilterContainInvokeMethodStr(String filterContainInvokeMethodStr) {
        this.filterContainInvokeMethodStr = filterContainInvokeMethodStr;
    }
}

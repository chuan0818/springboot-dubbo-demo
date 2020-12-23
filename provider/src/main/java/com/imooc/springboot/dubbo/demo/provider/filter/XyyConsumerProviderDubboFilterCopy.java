package com.imooc.springboot.dubbo.demo.provider.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imooc.springboot.dubbo.demo.provider.config.CommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.alibaba.dubbo.common.Constants.CONSUMER;
import static com.alibaba.dubbo.common.Constants.PROVIDER;

/**
 * PS:在dubbo的拦截器中，采用@Autowired 自动注入是无效的
 * 在dubbo的拦截器中，可以采取通过setter方式来注入其他的bean，且不要标注注解
 * //Could not resolve placeholder 'xxx' in value "${aaa.bbb.ccc}" 所以我们可以采用赋默认值的方式来防止这个错误
 * //@Value("${aaa.bbb.ccc:DefaultValue}") dubbo Filter无法注入@Value
 * 参考:https://blog.csdn.net/wuge507639721/article/details/82191365
 * 已验证最外层对象参数需要加class字段属性,对象参数内部的对象属性不用加class字段属性。如:PersonDto.otherDto(内部otherDto不需要class字段属性)
 * 加class属性可以通过paramStr.startsWith("{")判断【包括了对象和map】
 */
@Slf4j
@Activate(group = {CONSUMER, PROVIDER})
@Component
public class XyyConsumerProviderDubboFilterCopy implements Filter {

    //@Value("${xyydubbo.filterContainInvokeMethodStr:}") //逗号分隔
    //private String filterContainInvokeMethodStr;
    //@Value("${xyydubbo.skipInvokeMethodStr:}")
    //private String skipInvokeMethodStr=null;

    //方式1:这种方式，配合setter/getter可以直接实现依赖注入 (已验证-可行)
    //方式2:dubbo Filter内部拿到ApplicationContext (已验证-可行)
    private CommonConfig commonConfig;
    public CommonConfig getCommonConfig() {
        return commonConfig;
    }
    public void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ApplicationContext context= ServiceBean.getSpringContext();
        if(context != null) {
            commonConfig = context.getBean(CommonConfig.class);
        }

        String InvokeStatement = XyyConsumerProviderDubboFilter.buildInvokeStatement(invoker, invocation);
        long startTime = System.currentTimeMillis();
        Result result = invoker.invoke(invocation);
        long elapsed = System.currentTimeMillis() - startTime;
        String dubboPort = "";
        if(invoker.getUrl() != null) {
            dubboPort = String.valueOf(invoker.getUrl().getPort());
        }
        /*
        if(StringUtils.isEmpty(filterContainInvokeMethodStr)) {
            log.info("SpendTime={}ms,port={},{}", elapsed, dubboPort, InvokeStatement);
        }else{
            String[] containInvokeMethodStrArr = filterContainInvokeMethodStr.split(",");
            List<String> containInvokeMethodStrList = Arrays.asList(containInvokeMethodStrArr);
            if(containInvokeMethodStrList != null && containInvokeMethodStrList.size() > 0) {
                for (String containInvokeMethodStr : containInvokeMethodStrList) {
                    if(InvokeStatement.contains(containInvokeMethodStr)) {
                        log.info("SpendTime={}ms,port={},{}", elapsed, dubboPort, InvokeStatement);
                        break;
                    }
                }
            }else{
                log.info("SpendTime={}ms,port={},{}", elapsed, dubboPort, InvokeStatement);
            }
        }*/
        log.info("SpendTime={}ms,port={},{}", elapsed, dubboPort, InvokeStatement);
        return result;
    }


}


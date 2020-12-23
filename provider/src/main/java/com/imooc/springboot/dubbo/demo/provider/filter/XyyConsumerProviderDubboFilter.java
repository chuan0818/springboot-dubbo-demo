package com.imooc.springboot.dubbo.demo.provider.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imooc.springboot.dubbo.demo.provider.config.CommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.alibaba.dubbo.common.Constants.CONSUMER;
import static com.alibaba.dubbo.common.Constants.PROVIDER;

@Slf4j
@Activate(group = {CONSUMER, PROVIDER})
public class XyyConsumerProviderDubboFilter implements Filter {
    //方式2:dubbo Filter内部拿到ApplicationContext (可行)
    private CommonConfig commonConfig;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ApplicationContext context= ServiceBean.getSpringContext();
        if(context != null) {
            commonConfig = context.getBean(CommonConfig.class);
        }
        if(commonConfig != null) {
            log.info("commonConfig:{}", JSON.toJSONString(commonConfig));
        }


        String InvokeStatement = buildInvokeStatement(invoker, invocation);
        long startTime = System.currentTimeMillis();
        Result result = invoker.invoke(invocation);
        long elapsed = System.currentTimeMillis() - startTime;
        String dubboPort = "";
        if(invoker.getUrl() != null) {
            dubboPort = String.valueOf(invoker.getUrl().getPort());
        }
        log.info("SpendTime={}ms,port={},{}", elapsed, dubboPort, InvokeStatement);
        return result;
    }

    public static String buildInvokeStatement(Invoker<?> invoker, Invocation invocation) {
        String finalInvokeStatement = "";
        try{
            finalInvokeStatement = "invoke " + invocation.getInvoker().getInterface().getName() + "."+invocation.getMethodName();
            StringBuilder invokeStatement = new StringBuilder(finalInvokeStatement);
            invokeStatement.append("(");
            Object[] paramArr = invocation.getArguments();
            if(paramArr != null && paramArr.length > 0) {
                for (int i = 0; i < paramArr.length ; i++) {
                    if(paramArr[i] instanceof String) { //字符串
                        invokeStatement.append("\""+paramArr[i]+"\"");
                    }else if(paramArr[i] instanceof Number){ //数值
                        invokeStatement.append(paramArr[i]);
                    }else if(paramArr[i] instanceof Date){ //日期类型
                        //invokeStatement.append("\""+paramArr[i]+"\""); //"Tue Dec 22 22:43:19 CST 2020"解析成这样报错
                        invokeStatement.append(((Date)paramArr[i]).getTime()); //解析成时间戳(验证通过)-(参考json序列化)
                    } else if(paramArr[i] instanceof Object) { //对象类型
                        String paramStr = JSON.toJSONString(paramArr[i]);
                        if(!StringUtils.isEmpty(paramStr) && paramStr.startsWith("{")) {
                            JSONObject jsonObject = JSON.parseObject(paramStr);
                            jsonObject.put("class", paramArr[i].getClass());
                            invokeStatement.append(jsonObject.toString());
                        }else{ //兼容最外层列表或者数组参数
                            invokeStatement.append(paramStr);
                        }
                    }else{ //最外参数且是null(参考json序列化)
                        //log.info("not match type String/Number/Object(处理最外层值为null的参数为null):" + paramArr[i]);
                        invokeStatement.append(paramArr[i]);
                    }
                    if(i != paramArr.length-1){
                        invokeStatement.append(",");
                    }
                }
            }
            invokeStatement.append(")");
            finalInvokeStatement =  invokeStatement.toString();
        }catch (Exception e) {
            log.info("buildInvokeStatement error:" + finalInvokeStatement);
        }
        return finalInvokeStatement;
    }



}


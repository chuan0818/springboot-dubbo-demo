package com.imooc.springboot.dubbo.demo.provider.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
 */
@Slf4j
@Activate(group = {CONSUMER, PROVIDER})
@Component
public class XyyConsumerProviderDubboFilterCopy implements Filter {

    //@Value("${xyydubbo.filterContainInvokeMethodStr:}") //逗号分隔
    //private String filterContainInvokeMethodStr;
    //@Value("${xyydubbo.skipInvokeMethodStr:}")
    //private String skipInvokeMethodStr=null;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String InvokeStatement = buildInvokeStatement(invoker, invocation);
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

    private String buildInvokeStatement(Invoker<?> invoker, Invocation invocation) {
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
                    }else if(paramArr[i] instanceof Object) { //对象类型
                        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(paramArr[i]));
                        jsonObject.put("class", paramArr[i].getClass());
                        invokeStatement.append(jsonObject.toString());
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


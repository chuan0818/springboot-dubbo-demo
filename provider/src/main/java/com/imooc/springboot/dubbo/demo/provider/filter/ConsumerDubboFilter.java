package com.imooc.springboot.dubbo.demo.provider.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import static com.alibaba.dubbo.common.Constants.CONSUMER;
import static com.alibaba.dubbo.common.Constants.PROVIDER;

/**
 * 拦截打印dubbo请求参数
 *
 * invoke com.imooc.springboot.dubbo.demo.provider.service.Demo01Service.doSomeThing(null,{"addressList":["addr1","addr2","addr3"],"sex":1,"name":"xxx","class":"com.imooc.springboot.dubbo.demo.provider.dto.PersonDto","age":27},{"birthday":1608649640426,"str":"strDemo","longNum":1000,"xxxList":["xxx01","xxx02"],"num":10,"class":"com.imooc.springboot.dubbo.demo.provider.dto.OtherDto"},10,818.0704,null)
 * 报错: 仅返回null  参考:https://blog.csdn.net/flushofhope/article/details/108283791
 * 本质原因:com.alibaba.dubbo.rpc.protocol.dubbo.telnet.InvokeTelnetHandler#isMatch(java.lang.Class[], java.util.List)
 *         Object arg = args.get(i);
 *         if (ReflectUtils.isPrimitive(arg.getClass())) {  //外层参数传null,arg.getClass()会导致NPE
 * 当arg为null的时候getClass肯定会抛出异常,这个就是为什么我们invoke调用的时候如果参数存在为空的情况 会返回空的原因了。 dubbo2.6.2存在这个问题
 * dubbo在2.6.7版本修复了这个问题(已验证)
 * PS:外层null值参数会处理成null,json对象中值为null字段会被dubbo序列化时自动剔除
 */
@Slf4j
//@Activate(group = {CONSUMER})
public class ConsumerDubboFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        log.info("ConsumerDubboFilter拦截invoke...start");
        String invokeStatement = XyyConsumerProviderDubboFilter.buildInvokeStatement(invoker, invocation);
        //log.info("InterfaceName={},MethodName={},Parameter={}", invocation.getInvoker().getInterface().getName(), invocation.getMethodName(), invocation.getArguments());
        //开始时间
        long startTime = System.currentTimeMillis();
        //执行接口调用逻辑
        Result result = invoker.invoke(invocation);
        //调用耗时
        long elapsed = System.currentTimeMillis() - startTime;
        //如果发生异常 则打印异常日志
        //if (result.hasException() && invoker.getInterface() != GenericService.class) {
        //    log.error("dubbo执行异常: ", result.getException());
        //} else {
        //    log.info("InterfaceName={},MethodName={},Resposne={},SpendTime={} ms", invocation.getInvoker().getInterface().getName(), invocation.getMethodName(), JSON.toJSONString(new Object[]{result.getValue()}), elapsed);
        //}
        //返回结果响应结果
        String dubboPort = "";
        if(invoker.getUrl() != null) {
            dubboPort = String.valueOf(invoker.getUrl().getPort());
        }
        log.info("SpendTime={}ms,port={},{}", elapsed, dubboPort, invokeStatement);
        log.info("ConsumerDubboFilter拦截invoke...end");
        return result;
    }

    private String buildInvokeStatement(Invoker<?> invoker, Invocation invocation) {
        StringBuilder invokeStatement = new StringBuilder("invoke " + invocation.getInvoker().getInterface().getName() + "."+invocation.getMethodName());
        invokeStatement.append("(");
        Object[] paramArr = invocation.getArguments();
        if(paramArr != null && paramArr.length > 0) {
            for (int i = 0; i < paramArr.length ; i++) {
                //if(paramArr[i] == null) {
                //    continue;
                //}
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
                    log.info("not match type String/Number/Object(处理最外层值为null的参数为null):" + paramArr[i]);
                    invokeStatement.append(paramArr[i]);
                }
                if(i != paramArr.length-1){
                    invokeStatement.append(",");
                }
            }
        }
        invokeStatement.append(")");
        log.info(invokeStatement.toString());
        return invokeStatement.toString();
    }
}




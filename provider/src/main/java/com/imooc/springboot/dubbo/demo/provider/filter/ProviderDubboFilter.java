package com.imooc.springboot.dubbo.demo.provider.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.alibaba.dubbo.common.Constants.CACHE_KEY;
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
/**
 * org.apache.dubbo.Filter扩展点激活 CONSUMER消费者, PROVIDER提供者 已验证可替代
 * @Activate(group = {CONSUMER, PROVIDER}) 替代yml中dubbo Filter配置
 * consumer:
 *     filter: consumerDubboFilter  #配置dubbo filter(拦截dubbo消费者),必须和com.alibaba.dubbo.rpc.Filter文件中的值项的key保持一致
 * provider:
 *   filter: providerDubboFilter
 */
//@Activate(group = {PROVIDER})
public class ProviderDubboFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        log.info("ProviderDubboFilter拦截invoke...start");
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
        log.info("ProviderDubboFilter拦截invoke...end");
        return result;
    }


}




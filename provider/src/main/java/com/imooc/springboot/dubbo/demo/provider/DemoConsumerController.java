package com.imooc.springboot.dubbo.demo.provider;

import com.alibaba.dubbo.config.annotation.Reference;
import com.imooc.springboot.dubbo.demo.provider.dto.OtherDto;
import com.imooc.springboot.dubbo.demo.provider.dto.PersonDto;
import com.imooc.springboot.dubbo.demo.provider.service.Demo01Service;
import com.imooc.springboot.dubbo.demo.provider.service.DemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * http://127.0.0.1:8099/sayHello?name=xxxx
 * http://127.0.0.1:8099/doSomeThing
 * 既是dubbo服务消费者也是提供者
 */
@RestController
public class DemoConsumerController {

    @Reference
    private DemoService demoService;
    @Reference
    private Demo01Service demo01Service;

    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }

    @RequestMapping("/doSomeThing")
    public PersonDto doSomeThing() {
        //doSomeThing(String desc, PersonDto personDto, OtherDto otherDto, int count)
        PersonDto personDto = new PersonDto();
        personDto.setName("xxx");
        personDto.setAge(27);
        personDto.setSex((byte)1);
        List addressList = new ArrayList();
        addressList.add("addr1");
        addressList.add("addr2");
        addressList.add("addr3");
        personDto.setBirthDate(new Date());
        personDto.setBirthDate(null);
        Map paramMap01 = new HashMap<String, Object>();
        paramMap01.put("key1", "value1");
        personDto.setParamMap(paramMap01);
        personDto.setAddressList(addressList);

        OtherDto otherDto = new OtherDto();
        otherDto.setStr("strDemo");
        otherDto.setNum(10);
        otherDto.setLongNum(1000L);
        otherDto.setBirthday(new Date());
        otherDto.setXxxList(new ArrayList<String>(){{add("xxx01"); add("xxx02");}});
        personDto.setOtherDto(otherDto);
        Date now = new Date();
        now = null;

        List<String> paramList = new ArrayList<String>();
        paramList.add("aaa");
        paramList.add("bbb");

        Map paramMap = new HashMap<String, Object>();
        paramMap.put("key", "value");
        return demo01Service.doSomeThing("zzc desc", personDto, otherDto, 10,
                new BigDecimal("818.0704"), new Date(), paramList, paramMap);
    }

}
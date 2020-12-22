package com.imooc.springboot.dubbo.demo.provider;

import com.alibaba.dubbo.config.annotation.Reference;
import com.imooc.springboot.dubbo.demo.DemoService;
import com.imooc.springboot.dubbo.demo.provider.dto.OtherDto;
import com.imooc.springboot.dubbo.demo.provider.dto.PersonDto;
import com.imooc.springboot.dubbo.demo.provider.service.Demo01Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        personDto.setAddressList(addressList);

        OtherDto otherDto = new OtherDto();
        otherDto.setStr("strDemo");
        otherDto.setNum(10);
        otherDto.setLongNum(1000L);
        otherDto.setBirthday(new Date());
        otherDto.setXxxList(new ArrayList<String>(){{add("xxx01"); add("xxx02");}});
        Date now = new Date();
        now = null;
        return demo01Service.doSomeThing(null, personDto, otherDto, 10, new BigDecimal("818.0704"), null);
    }

}
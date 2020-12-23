package com.imooc.springboot.dubbo.demo.provider.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.imooc.springboot.dubbo.demo.provider.dto.OtherDto;
import com.imooc.springboot.dubbo.demo.provider.dto.PersonDto;
import com.imooc.springboot.dubbo.demo.provider.service.Demo01Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Demo01ServiceImpl implements Demo01Service {

    @Override
    public PersonDto doSomeThing(String desc, PersonDto personDto, OtherDto otherDto, int count, BigDecimal decimalVar, Date date,
                                 List<String> paramList, Map<String, Object> paramMap) {
        //log.info("最外层接收到的时间参数:"+ date);
        PersonDto personDtoNew = new PersonDto();
        BeanUtils.copyProperties(personDto, personDtoNew);
        personDtoNew.setName("new "+personDto.getName());
        return personDtoNew;
    }
}

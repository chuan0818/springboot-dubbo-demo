package com.imooc.springboot.dubbo.demo.provider.service;

import com.imooc.springboot.dubbo.demo.provider.dto.OtherDto;
import com.imooc.springboot.dubbo.demo.provider.dto.PersonDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Demo01Service {
    public PersonDto doSomeThing(String desc, PersonDto personDto, OtherDto otherDto,
                                 int count, BigDecimal decimalVar, Date date, List<String> paramList,
                                 Map<String, Object> paramMap);
}

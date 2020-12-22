package com.imooc.springboot.dubbo.demo.provider.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OtherDto implements Serializable {
    private String       str;
    private Integer      num;
    private Long         longNum;
    private Date         birthday;
    private List<String> xxxList;

    public java.lang.String getStr() {
        return str;
    }

    public void setStr(java.lang.String str) {
        this.str = str;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getLongNum() {
        return longNum;
    }

    public void setLongNum(Long longNum) {
        this.longNum = longNum;
    }

    public List<java.lang.String> getXxxList() {
        return xxxList;
    }

    public void setXxxList(List<java.lang.String> xxxList) {
        this.xxxList = xxxList;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}

package com.imooc.springboot.dubbo.demo.provider.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PersonDto implements Serializable {

    private String       name;
    private int          age;
    private byte         sex;
    private List<String> addressList;
    private Date         birthDate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
}

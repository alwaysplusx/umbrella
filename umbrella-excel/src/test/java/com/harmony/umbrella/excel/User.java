package com.harmony.umbrella.excel;

import java.util.Date;

import com.harmony.umbrella.excel.annotation.ExcelColumn;

/**
 * @author wuxii@foxmail.com
 */
public class User {

    @ExcelColumn(0)
    private String name;

    @ExcelColumn(1)
    private int age;

    @ExcelColumn(2)
    private Date birthday;

    public User() {
    }

    public User(String name, int age, Date birthday) {
        this.name = name;
        this.age = age;
        this.birthday = birthday;
    }

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "{name:" + name + ", age:" + age + ", birthday:" + birthday + "}";
    }

}

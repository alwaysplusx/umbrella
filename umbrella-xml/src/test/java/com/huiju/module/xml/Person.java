package com.huiju.module.xml;

import java.util.Date;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class Person {

    private String name;
    private int age;
    private Date birthday;

    private List<Person> children;

    private Person father;

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

    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    @Override
    public String toString() {
        return "Person: {name:" + name + ", age:" + age + ", birthday:" + birthday + ", children:" + children + ", father:" + father + "}";
    }

}

package com.harmony.umbrella.validator;

/**
 * @author wuxii@foxmail.com
 */
public class Bar {

    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "{\"age\":\"" + age + "\"}";
    }

}

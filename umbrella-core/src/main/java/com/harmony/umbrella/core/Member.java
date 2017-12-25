package com.harmony.umbrella.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 成员变量， 通过getter/setter方式来获取，如果getter/setter都不存在则使用field方式
 * 
 * @author wuxii@foxmail.com
 */
public interface Member {

    /**
     * 成员的名称
     * 
     * @return name of member
     */
    String getName();

    /**
     * 拥有此成员变量的类型
     * 
     * @return owner type
     */
    Class<?> getOwnerType();

    /**
     * 成员变量类型
     * 
     * @return 成员变量类型
     */
    Class<?> getType();

    /**
     * 成员变量的字段
     * 
     * @return 成员字段
     */
    Field getField();

    /**
     * 成员变量的getter方法
     * 
     * @return getter method of member
     */
    Method getReadMethod();

    /**
     * 成员变量的setter方法
     * 
     * @return setter method of member
     */
    Method getWriteMethod();

    /**
     * 验证成员变量是否可读
     * 
     * @return 可读性, true is readable, false is not
     */
    boolean isReadable();

    /**
     * 成员变量的可写性
     * 
     * @return 可写性, true is writeable, false is not
     */
    boolean isWriteable();

    /**
     * 成员变量相关的注解
     * 
     * @param annCls
     *            注解类
     * @return 成员变量上的注解
     */
    <T extends Annotation> T getAnnotation(Class<T> annCls);

    /**
     * 读取目标中的成员变量, 优先采用getter方法读取成员值, 如果无法通过方法读取则采用字段反射的方式来取值
     * 
     * @param target
     *            目标对象
     * @return 成员变量值
     */
    Object get(Object target);

    /**
     * 设置目标的成员值, 优先采用setter方法设置成员值, 如果无法通过方法设置则采用字段反射的方式来设置
     * 
     * @param target
     *            目标对象
     * @param val
     *            设如的值
     */
    void set(Object target, Object val);

    /**
     * 同owner下的相关成员变量
     * 
     * @param name
     *            成员名称
     * @return 成员member
     */
    Member createRelative(String name);

}

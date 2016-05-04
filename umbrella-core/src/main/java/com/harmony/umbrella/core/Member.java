package com.harmony.umbrella.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 成员变量， 通过getter/setter方式来获取，如果getter/setter都不存在则使用field方式
 * 
 * @author wuxii@foxmail.com
 */
public interface Member {

    String getName();

    Class<?> getOwnerType();

    Class<?> getType();

    Field getField();

    Method getReadMethod();

    Method getWriteMethod();

    boolean isReadable();

    boolean isWriteable();

    Object get(Object obj);

    void set(Object obj, Object val);

    Member createRelative(String name);

}

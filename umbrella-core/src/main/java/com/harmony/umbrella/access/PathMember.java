package com.harmony.umbrella.access;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * field / getter or setter method
 * 
 * @author wuxii@foxmail.com
 */
public class PathMember implements Member {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Class<?> getOwnerType() {
        return null;
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public Field getField() {
        return null;
    }

    @Override
    public Method getReadMethod() {
        return null;
    }

    @Override
    public Method getWriteMethod() {
        return null;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean isWriteable() {
        return false;
    }

    @Override
    public Object get(Object obj) {
        return null;
    }

    @Override
    public void set(Object obj, Object val) {
    }

}
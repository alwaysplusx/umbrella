package com.harmony.umbrella.access.impl;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.harmony.umbrella.access.AbstractAccess;

/**
 * @author wuxii@foxmail.com
 */
public class ReflectionAccess extends AbstractAccess {

    @Override
    public boolean isAccessible(String name, Object target) {
        Field field = findField(target.getClass(), name);
        if (field == null) {
            return findReadMethod(target.getClass(), name) != null && findWriterMethod(target.getClass(), name) != null;
        }
        return true;
    }

    @Override
    protected Object getNameValue(String name, Object target) {
        Method method = findReadMethod(target.getClass(), name);
        if (method != null) {
            return invokeMethod(method, target);
        }
        return getFieldValue(name, target);
    }

    @Override
    protected void setNameValue(String name, Object target, Object value) {
        Method method = findWriterMethod(target.getClass(), name);
        if (method != null) {
            invokeMethod(method, target, value);
        } else {
            setFieldValue(name, target, value);
        }
    }
}

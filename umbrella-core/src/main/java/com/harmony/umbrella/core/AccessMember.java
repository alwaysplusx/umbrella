package com.harmony.umbrella.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ReflectionUtils;

public class AccessMember {

    private Method method;
    private Field field;
    private final Class<?> targetClass;

    private AccessMember(Class<?> targetClass, Field field) {
        this.targetClass = targetClass;
        this.field = field;
    }

    private AccessMember(Class<?> targetClass, Method method) {
        this.targetClass = targetClass;
        this.method = method;
    }

    public Object get(Object target) {
        checkTargetClass(target);
        Assert.isTrue(method != null || field != null, "target method or field not set");
        if (method == null) {
            try {
                method = ReflectionUtils.findReadMethod(targetClass, field);
            } catch (NoSuchMethodException e) {
                return ReflectionUtils.getFieldValue(field, target);
            }
        }
        return ReflectionUtils.invokeMethod(method, target);
    }

    public void set(Object target, Object value) {
        checkTargetClass(target);
        Assert.isTrue(method != null || field != null, "target method or field not set");
        if (method == null) {
            try {
                method = ReflectionUtils.findReadMethod(targetClass, field);
            } catch (NoSuchMethodException e) {
                ReflectionUtils.setFieldValue(field, target, value);
            }
        } else {
            ReflectionUtils.invokeMethod(method, target, value);
        }

    }

    public void checkTargetClass(Object target) {
        if (!targetClass.isAssignableFrom(target.getClass())) {
            throw new IllegalArgumentException("target object not match");
        }
    }

}
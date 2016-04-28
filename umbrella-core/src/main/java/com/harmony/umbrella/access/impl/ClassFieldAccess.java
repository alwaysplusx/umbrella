package com.harmony.umbrella.access.impl;

import com.harmony.umbrella.access.AbstractAccess;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ClassFieldAccess extends AbstractAccess {

    @Override
    public boolean isAccessible(String name, Object target) {
        return target instanceof Class && StringUtils.isNotBlank(name);
    }

    @Override
    protected Object getNameValue(String name, Object target) {
        return ReflectionUtils.findField((Class<?>) target, name);
    }

    @Override
    protected void setNameValue(String name, Object target, Object value) {
        throw new UnsupportedOperationException("unsupport set class field");
    }

}

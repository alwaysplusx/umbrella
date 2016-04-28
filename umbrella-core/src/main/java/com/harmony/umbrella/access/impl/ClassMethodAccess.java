package com.harmony.umbrella.access.impl;

import com.harmony.umbrella.access.AbstractAccess;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ClassMethodAccess extends AbstractAccess {

    @Override
    public boolean isAccessible(String name, Object target) {
        return target instanceof Class && StringUtils.isNotBlank(name);
    }

    @Override
    public Object getNameValue(String name, Object target) {
        return ReflectionUtils.findMethod((Class<?>) target, name);
    }

    @Override
    public void setNameValue(String name, Object target, Object value) {
        throw new UnsupportedOperationException("unsupport set class method");
    }

}

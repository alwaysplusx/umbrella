package com.harmony.umbrella.access.impl;

import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.access.AbstractAccess;

/**
 * @author wuxii@foxmail.com
 */
public class MapAccess extends AbstractAccess {

    @Override
    public boolean isAccessible(String name, Object target) {
        if (target instanceof Map) {
            if (name == null) {
                return target instanceof HashMap;
            }
        }
        return false;
    }

    @Override
    protected Object getNameValue(String name, Object target) {
        return ((Map<?, ?>) target).get(name);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void setNameValue(String name, Object target, Object value) {
        ((Map) target).put(name, value);
    }

}

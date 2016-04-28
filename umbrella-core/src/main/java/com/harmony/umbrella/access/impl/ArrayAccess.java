package com.harmony.umbrella.access.impl;

import com.harmony.umbrella.access.AbstractAccess;
import com.harmony.umbrella.util.DigitUtils;

import java.lang.reflect.Array;

/**
 * @author wuxii@foxmail.com
 */
public class ArrayAccess extends AbstractAccess {

    @Override
    public boolean isAccessible(String name, Object target) {
        if (target == null) {
            return false;
        }
        return target.getClass().isArray() && DigitUtils.isDigit(name);
    }

    @Override
    protected Object getNameValue(String name, Object target) {
        return Array.get(target, Integer.parseInt(name));
    }

    @Override
    protected void setNameValue(String name, Object target, Object value) {
        Array.set(target, Integer.parseInt(name), value);
    }
}

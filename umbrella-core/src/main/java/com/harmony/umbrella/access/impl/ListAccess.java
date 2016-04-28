package com.harmony.umbrella.access.impl;

import com.harmony.umbrella.access.AbstractAccess;
import com.harmony.umbrella.util.DigitUtils;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class ListAccess extends AbstractAccess {

    @Override
    public boolean isAccessible(String name, Object target) {
        return target instanceof List && DigitUtils.isDigit(name);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Object getNameValue(String name, Object target) {
        return ((List) target).get(Integer.parseInt(name));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void setNameValue(String name, Object target, Object value) {
        ((List) target).set(Integer.parseInt(name), value);
    }

}

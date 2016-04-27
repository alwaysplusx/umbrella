package com.harmony.umbrella.core.accessor;

import com.harmony.umbrella.util.DigitUtils;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class ListAccessor extends AbstractAccessor {

    public static final ListAccessor INSTANCE = new ListAccessor();

    @Override
    public boolean isAccessible(String name, Object target) {
        return target instanceof List && DigitUtils.isDigit(name);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getNameValue(String name, Object target) {
        return ((List) target).get(Integer.parseInt(name));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setNameValue(String name, Object target, Object value) {
        ((List) target).set(Integer.parseInt(name), value);
    }

}

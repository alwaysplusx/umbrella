package com.harmony.umbrella.access;

import com.harmony.umbrella.util.DigitUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ArrayAccessor extends CheckedAccessor<Object[]> {

    public ArrayAccessor() {
        super(Object[].class);
    }

    @Override
    public boolean support(String name) {
        return DigitUtils.isDigit(name);
    }

    @Override
    public Object get(String name, Object[] obj) {
        return obj[Integer.valueOf(name)];
    }

    @Override
    public void set(String name, Object[] obj, Object val) {
        obj[Integer.valueOf(name)] = val;
    }

}

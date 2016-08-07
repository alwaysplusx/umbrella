package com.harmony.umbrella.log.access;

import com.harmony.umbrella.util.NumberUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ArrayAccessor extends CheckedAccessor<Object[]> {

    public ArrayAccessor() {
        super(Object[].class);
    }

    @Override
    public boolean support(String name) {
        return NumberUtils.isNumber(name);
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

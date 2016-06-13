package com.harmony.umbrella.log.access;

import java.util.List;

import com.harmony.umbrella.util.DigitUtils;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class ListAccessor extends CheckedAccessor<List> {

    public ListAccessor() {
        super(List.class);
    }

    @Override
    public boolean support(String name) {
        return DigitUtils.isDigit(name);
    }

    @Override
    public Object get(String name, List obj) {
        return obj.get(Integer.valueOf(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(String name, List obj, Object val) {
        obj.set(Integer.valueOf(name), val);
    }
}

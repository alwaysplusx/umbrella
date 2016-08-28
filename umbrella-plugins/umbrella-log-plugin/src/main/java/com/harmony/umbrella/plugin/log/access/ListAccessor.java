package com.harmony.umbrella.plugin.log.access;

import java.util.List;

import com.harmony.umbrella.util.NumberUtils;

/**
 * List解析工具，通过index获取值
 * 
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class ListAccessor extends CheckedAccessor<List> {

    public ListAccessor() {
        super(List.class);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public boolean support(String name) {
        return NumberUtils.isNumber(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String name, List obj) {
        return obj.get(Integer.valueOf(name));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void set(String name, List obj, Object val) {
        obj.set(Integer.valueOf(name), val);
    }
}

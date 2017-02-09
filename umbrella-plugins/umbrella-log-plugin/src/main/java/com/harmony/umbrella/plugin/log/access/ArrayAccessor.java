package com.harmony.umbrella.plugin.log.access;

import java.math.BigDecimal;

import org.springframework.util.NumberUtils;

/**
 * 数组类型的属性解析工具，通过index获取值
 * 
 * @author wuxii@foxmail.com
 */
public class ArrayAccessor extends CheckedAccessor<Object[]> {

    public ArrayAccessor() {
        super(Object[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean support(String name) {
        try {
            NumberUtils.parseNumber(name, BigDecimal.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String name, Object[] obj) {
        return obj[Integer.valueOf(name)];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String name, Object[] obj, Object val) {
        obj[Integer.valueOf(name)] = val;
    }

}

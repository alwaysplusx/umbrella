package com.harmony.umbrella.plugin.log.access;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.util.NumberUtils;

/**
 * List解析工具，通过index获取值
 * 
 * @author wuxii@foxmail.com
 */
public class ListAccessor extends CheckedAccessor<List> {

    public ListAccessor() {
        super(List.class);
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
    public Object get(String name, List obj) {
        return obj.get(Integer.valueOf(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String name, List obj, Object val) {
        obj.set(Integer.valueOf(name), val);
    }
}

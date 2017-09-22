package com.harmony.umbrella.plugin.log.access;

import java.util.Map;

/**
 * map解析工具， 通过key获取值
 * 
 * @author wuxii@foxmail.com
 */
public class MapAccessor extends CheckedAccessor<Map> {

    public MapAccessor() {
        super(Map.class);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public boolean support(String name) {
        return true;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public Object get(String name, Map obj) {
        return obj.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String name, Map obj, Object val) {
        obj.put(name, val);
    }

}

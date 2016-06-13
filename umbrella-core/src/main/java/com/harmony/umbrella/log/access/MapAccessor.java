package com.harmony.umbrella.log.access;

import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class MapAccessor extends CheckedAccessor<Map> {

    public MapAccessor() {
        super(Map.class);
    }

    @Override
    public boolean support(String name) {
        return true;
    }

    @Override
    public Object get(String name, Map obj) {
        return obj.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(String name, Map obj, Object val) {
        obj.put(name, val);
    }

}

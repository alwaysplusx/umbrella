package com.harmony.umbrella.core.accessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public class MapAccessor extends AbstractAccessor {

    public static final MapAccessor INSTANCE = new MapAccessor();

    @Override
    public boolean isAccessible(String name, Object target) {
        if (target instanceof Map) {
            if (name == null) {
                return target instanceof HashMap;
            }
        }
        return false;
    }

    @Override
    public Object getNameValue(String name, Object target) {
        return ((Map<?, ?>) target).get(name);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setNameValue(String name, Object target, Object value) {
        ((Map) target).put(name, value);
    }

}

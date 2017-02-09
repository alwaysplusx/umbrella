package com.harmony.umbrella.plugin.log.access;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.NumberUtils;

/**
 * 元数据数组的解析工具
 * 
 * @author wuxii@foxmail.com
 */
public class PrimitiveArrayAccessor extends CheckedAccessor<Object> {

    private static final Map<Class<?>, Class<?>> primitiveArray = new HashMap<Class<?>, Class<?>>();

    static {
        primitiveArray.put(boolean[].class, Boolean[].class);
        primitiveArray.put(byte[].class, Byte[].class);
        primitiveArray.put(char[].class, Character[].class);
        primitiveArray.put(double[].class, Double[].class);
        primitiveArray.put(float[].class, Float[].class);
        primitiveArray.put(int[].class, Integer[].class);
        primitiveArray.put(long[].class, Long[].class);
        primitiveArray.put(short[].class, Short[].class);
    }

    public PrimitiveArrayAccessor() {
        super(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String name, Object obj) {
        return Array.get(obj, Integer.valueOf(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String name, Object obj, Object val) {
        Array.set(obj, Integer.valueOf(name), val);
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

    public boolean support(String name, Object val) {
        try {
            NumberUtils.parseNumber(name, BigDecimal.class);
            return primitiveArray.containsKey(val.getClass());
        } catch (Exception e) {
            return false;
        }
    }

}
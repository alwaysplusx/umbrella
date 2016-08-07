package com.harmony.umbrella.log.access;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.util.NumberUtils;

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

    @Override
    public Object get(String name, Object obj) {
        return Array.get(obj, Integer.valueOf(name));
    }

    @Override
    public void set(String name, Object obj, Object val) {
        Array.set(obj, Integer.valueOf(name), val);
    }

    @Override
    public boolean support(String name) {
        return NumberUtils.isNumber(name);
    }

    public boolean support(String name, Object val) {
        return NumberUtils.isNumber(name) && primitiveArray.containsKey(val.getClass());
    }

}
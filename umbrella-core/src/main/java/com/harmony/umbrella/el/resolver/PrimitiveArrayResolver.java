package com.harmony.umbrella.el.resolver;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.DigitUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PrimitiveArrayResolver extends CheckedResolver<Object> {

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

    public PrimitiveArrayResolver(int priority) {
        super(priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return obj != null && DigitUtils.isDigit(name) && primitiveArray.containsKey(obj.getClass());
    }

    @Override
    protected Object doResolve(String name, Object obj) {
        return Array.get(obj, Integer.valueOf(name));
    }

}

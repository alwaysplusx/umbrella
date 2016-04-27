package com.harmony.umbrella.core.accessor;

import com.harmony.umbrella.util.DigitUtils;

import java.lang.reflect.Array;

/**
 * @author wuxii@foxmail.com
 */
public class ArrayAccessor extends AbstractAccessor {

    public static final ArrayAccessor INSTANCE = new ArrayAccessor();
    
    @Override
    public boolean isAccessible(String name, Object target) {
        if (target == null) {
            return false;
        }
        return target.getClass().isArray() && DigitUtils.isDigit(name);
    }

    @Override
    public Object getNameValue(String name, Object target) {
        return Array.get(target, Integer.parseInt(name));
    }

    @Override
    public void setNameValue(String name, Object target, Object value) {
        Array.set(target, Integer.parseInt(name), value);
    }
}

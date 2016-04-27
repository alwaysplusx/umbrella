package com.harmony.umbrella.core.accessor;

import java.lang.reflect.Field;

import static com.harmony.umbrella.util.ReflectionUtils.*;

/**
 * @author wuxii@foxmail.com
 */
public class ReflectionAccessor extends AbstractAccessor {

    @Override
    public Class<?> getType(String name, Object target) {
        Field field = findField(target.getClass(), name);
        if (field == null) {
            throw new IllegalArgumentException(target + " " + name + " not accessible");
        }
        return field.getType();
    }

    @Override
    public boolean isAccessible(String name, Object target) {
        return findField(target.getClass(), name) != null;
    }

    @Override
    public Object getNameValue(String name, Object target) {
        return getFieldValue(name, target);
    }

    @Override
    public void setNameValue(String name, Object target, Object value) {
        setFieldValue(name, target, value);
    }
}

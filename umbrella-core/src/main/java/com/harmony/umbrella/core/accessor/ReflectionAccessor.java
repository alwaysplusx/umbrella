package com.harmony.umbrella.core.accessor;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public class ReflectionAccessor extends AbstractAccessor {

    public static final ReflectionAccessor INSTANCE = new ReflectionAccessor();

    @Override
    public boolean isAccessible(String name, Object target) {
        Field field = findField(target.getClass(), name);
        if (field == null) {
            try {
                findReadMethod(target.getClass(), name);
                findWriterMethod(target.getClass(), name);
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getNameValue(String name, Object target) {
        try {
            Method method = findReadMethod(target.getClass(), name);
            return invokeMethod(method, target);
        } catch (NoSuchMethodException e) {
            return getFieldValue(name, target);
        }
    }

    @Override
    public void setNameValue(String name, Object target, Object value) {
        try {
            Method method = findWriterMethod(target.getClass(), name);
            invokeMethod(method, target, value);
        } catch (NoSuchMethodException e) {
            setFieldValue(name, target, value);
        }
    }
}

package com.harmony.umbrella.core;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * field / getter or setter method
 * 
 * @author wuxii@foxmail.com
 */
public class AccessMember {

    private final Class<?> targetClass;

    private Field field;
    private Method readMethod;
    private Method writeMethod;

    public AccessMember(Class<?> targetClass, String fieldName) {
        this.targetClass = targetClass;
        this.field = findField(targetClass, fieldName);
    }

    public AccessMember(Class<?> targetClass, Field field) {
        this.targetClass = targetClass;
        this.field = field;
    }

    public AccessMember(Class<?> targetClass, Method readMethod) {
        this.targetClass = targetClass;
        this.readMethod = readMethod;
    }

    public AccessMember(Class<?> targetClass, Method readMethod, Method writeMethod) {
        this.targetClass = targetClass;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    public String getName() {
        if (field != null) {
            return field.getName();
        }
        Method method = readMethod != null ? readMethod : writeMethod;
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(4);
    }

    public Class<?> getType() {
        if (field != null) {
            return field.getType();
        }
        Method method = getReadMethod();
        if (method != null) {
            return method.getReturnType();
        }
        return getWriteMethod().getParameterTypes()[0];
    }

    public Object get(Object target) {
        if (!isTarget(target)) {
            throw new IllegalArgumentException(target + " not " + targetClass.getName() + " object");
        }
        Method method = getReadMethod();
        if (method != null) {
            return invokeMethod(method, target);
        }
        return getFieldValue(field, target);
    }

    public void set(Object target, Object value) {
        if (!isTarget(target)) {
            throw new IllegalArgumentException(target + " not " + targetClass.getName() + " object");
        }
        Method method = getWriteMethod();
        if (method != null) {
            invokeMethod(method, target, value);
        } else {
            setFieldValue(field, target, value);
        }
    }

    public Method getReadMethod() {
        if (readMethod == null) {
            try {
                readMethod = findReadMethod(targetClass, getName());
            } catch (NoSuchMethodException e) {
            }
        }
        return readMethod;
    }

    public Method getWriteMethod() {
        if (writeMethod == null) {
            try {
                writeMethod = findWriterMethod(targetClass, getName());
            } catch (NoSuchMethodException e) {
            }
        }
        return writeMethod;
    }

    public Field getField() {
        if (field == null) {
            Method method = readMethod != null ? readMethod : writeMethod;
            String name = method.getName().substring(3);
            field = findField(targetClass, Character.toLowerCase(name.charAt(0)) + name.substring(4));
        }
        return field;
    }

    public boolean isTarget(Object target) {
        return target != null && targetClass.isInstance(target);
    }

}
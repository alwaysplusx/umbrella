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
    private final String name;

    private Field field;
    private Method readMethod;
    private Method writeMethod;

    public AccessMember(Class<?> targetClass, Field field) {
        this(targetClass, field.getName(), field);
    }

    public AccessMember(Class<?> targetClass, String fieldName) {
        this(targetClass, fieldName, findField(targetClass, fieldName));
    }

    private AccessMember(Class<?> targetClass, String fieldName, Field field) {
        this.targetClass = targetClass;
        this.name = fieldName;
        this.field = field;
    }

    public AccessMember(Class<?> targetClass, Method readMethod) {
        this(targetClass, readMethod, null, getMethodSimpleName(readMethod));
    }

    public AccessMember(Class<?> targetClass, Method readMethod, Method writeMethod) {
        this(targetClass, readMethod, writeMethod, getMethodSimpleName(readMethod));
    }

    public AccessMember(Class<?> targetClass, Method readMethod, Method writeMethod, String name) {
        this.targetClass = targetClass;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.name = name;
    }

    public final String getName() {
        return name;
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
                readMethod = findReadMethod(targetClass, name);
            } catch (NoSuchMethodException e) {
            }
        }
        return readMethod;
    }

    public Method getWriteMethod() {
        if (writeMethod == null) {
            try {
                writeMethod = findWriterMethod(targetClass, name);
            } catch (NoSuchMethodException e) {
            }
        }
        return writeMethod;
    }

    public Field getField() {
        if (field == null) {
            field = findField(targetClass, name);
        }
        return field;
    }

    public boolean isTarget(Object target) {
        return target != null && targetClass.isInstance(target);
    }

    private static String getMethodSimpleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(4);
    }

}
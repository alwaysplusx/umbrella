package com.harmony.umbrella.access;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class DefalutMember implements Member {

    protected final Class<?> ownerType;
    protected final String name;

    private Field field;
    private Class<?> type;
    private Method readMethod;
    private Method writeMethod;

    public DefalutMember(Class<?> ownerType, String name) {
        this(ownerType, name, findField(ownerType, name));
    }

    public DefalutMember(Field field) {
        this(field.getDeclaringClass(), field.getName(), field);
    }

    protected DefalutMember(Class<?> ownerType, String name, Field field) {
        this.ownerType = ownerType;
        this.name = name;
        this.field = field;
        this.type = field.getType();
    }

    public DefalutMember(Method readMethod) {
        this(readMethod, null);
    }

    public DefalutMember(Method readMethod, Method writeMethod) {
        Method delegate = readMethod != null ? readMethod : writeMethod;
        if (delegate == null) {
            throw new IllegalArgumentException("getter and setter are null");
        }
        this.ownerType = delegate.getDeclaringClass();
        this.name = getMethodSimpleName(delegate);
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getOwnerType() {
        return ownerType;
    }

    @Override
    public Class<?> getType() {
        if (type == null) {
            // 通过token找字段
            Field f = getField();
            if (f != null) {
                // 使用field的类型设置接下去要查找的token
                return type = f.getType();
            }
            // 未找到通过getter方法查找
            Method m = getReadMethod();
            if (m != null) {
                return type = m.getReturnType();
            }
            // 通过setter方法查找
            m = getWriteMethod();
            if (m != null) {
                return type = m.getParameterTypes()[0];
            }
            throw new IllegalArgumentException(ownerType + " no such member " + name);
        }
        return type;
    }

    @Override
    public Field getField() {
        if (field == null) {
            field = findField(ownerType, name);
        }
        return field;
    }

    @Override
    public Method getReadMethod() {
        if (readMethod == null) {
            readMethod = findReadMethod(ownerType, name);
        }
        return null;
    }

    @Override
    public Method getWriteMethod() {
        if (writeMethod == null) {
            writeMethod = findWriterMethod(ownerType, name);
        }
        return writeMethod;
    }

    @Override
    public boolean isReadable() {
        return getField() != null || getReadMethod() != null;
    }

    @Override
    public boolean isWriteable() {
        return getField() != null || getWriteMethod() != null;
    }

    @Override
    public Object get(Object obj) {
        Assert.isTrue(isOwner(obj), obj + " not instance of " + ownerType);
        Assert.isTrue(isReadable(), ownerType + " " + name + " not readable");
        Method method = getReadMethod();
        if (method != null) {
            return invokeMethod(method, obj);
        }
        return getFieldValue(getField(), obj);
    }

    @Override
    public void set(Object obj, Object val) {
        Assert.isTrue(isOwner(obj), obj + " not instance of " + ownerType);
        Assert.isTrue(isWriteable(), ownerType + " " + name + " not writeable");
        Method method = getWriteMethod();
        if (method != null) {
            invokeMethod(method, obj, val);
        } else {
            setFieldValue(getField(), obj, val);
        }
    }

    public boolean isOwner(Object obj) {
        return ownerType.isInstance(obj);
    }

    protected Object getTokenValue(String token, Object target) {
        Method method = findReadMethod(target.getClass(), token);
        if (method != null) {
            return invokeMethod(method, target);
        }
        return getFieldValue(token, target);
    }

    protected static String getMethodSimpleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(4);
    }
}

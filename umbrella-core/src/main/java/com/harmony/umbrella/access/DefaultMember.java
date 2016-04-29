package com.harmony.umbrella.access;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
@Deprecated
public class DefaultMember implements Member {

    protected final Class<?> ownerType;
    protected final String name;

    private Field field;
    private Class<?> type;
    private Method readMethod;
    private Method writeMethod;

    public DefaultMember(Class<?> ownerType, String name, Field field) {
        this.ownerType = ownerType;
        this.name = name;
        this.field = field;
        this.type = field.getType();
    }

    public DefaultMember(Class<?> ownerType, String name, Method readMethod) {
        this.ownerType = ownerType;
        this.name = name;
        this.readMethod = readMethod;
        this.type = readMethod.getReturnType();
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
        return getFieldValue(getField(), obj);
    }

    @Override
    public void set(Object obj, Object val) {
        Assert.isTrue(isOwner(obj), obj + " not instance of " + ownerType);
        Assert.isTrue(isWriteable(), ownerType + " " + name + " not writeable");
        setFieldValue(getField(), obj, val);
    }

    @Override
    public Member createRelative(String name) {
        Field relativeField = findField(ownerType, name);
        if (relativeField == null) {
            throw new IllegalArgumentException(ownerType + " not contains " + name);
        }
        return new DefaultMember(ownerType, name, relativeField);
    }

    public boolean isOwner(Object obj) {
        return ownerType.isInstance(obj);
    }
}

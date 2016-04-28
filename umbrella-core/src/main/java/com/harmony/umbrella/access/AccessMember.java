package com.harmony.umbrella.access;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.harmony.umbrella.util.Assert;

/**
 * field / getter or setter method
 * 
 * @author wuxii@foxmail.com
 */
public class AccessMember implements Member {

    private final Class<?> rootClass;
    private final String memberName;

    private Class<?> memberType;
    private Field field;
    private Method readMethod;
    private Method writeMethod;

    public AccessMember(Class<?> rootClass, String memberName) {
        this.rootClass = rootClass;
        this.memberName = memberName;
    }

    public AccessMember(Field field) {
        this.rootClass = field.getDeclaringClass();
        this.memberName = field.getName();
        this.field = field;
    }

    public AccessMember(Method readMethod) {
        this(readMethod, null);
    }

    public AccessMember(Method readMethod, Method writeMethod) {
        Method delegate = readMethod != null ? readMethod : writeMethod;
        if (delegate == null) {
            throw new IllegalArgumentException("getter and setter are null");
        }
        if (readMethod != null && !isReadMethod(readMethod)) {
            throw new IllegalArgumentException(readMethod + " not getter method");
        }
        if (writeMethod != null && !isWriteMethod(writeMethod)) {
            throw new IllegalArgumentException(writeMethod + " not writer method");
        }
        this.rootClass = delegate.getDeclaringClass();
        this.memberName = getMethodSimpleName(delegate);
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    @Override
    public Class<?> getOwnerType() {
        return rootClass;
    }

    @Override
    public String getName() {
        return getMemberName();
    }

    @Override
    public Class<?> getType() {
        return getMemberType();
    }

    public final String getMemberName() {
        return memberName;
    }

    public Class<?> getMemberType() {
        if (memberType == null) {
            Class<?> tmpClass = rootClass;
            StringTokenizer st = new StringTokenizer(memberName, ".");
            while (st.hasMoreTokens()) {
                tmpClass = getTokenType(st.nextToken(), tmpClass);
            }
            memberType = tmpClass;
        }
        return memberType;
    }

    public Field getField() {
        if (field == null) {
            Class<?> tmpClass = rootClass;
            StringTokenizer st = new StringTokenizer(memberName, ".");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                // 通过token找字段
                if (!st.hasMoreTokens()) {
                    // 找到最后的token，找到结果
                    field = findField(tmpClass, token);
                    break;
                }
                tmpClass = getTokenType(token, tmpClass);
            }
        }
        return field;
    }

    public Method getReadMethod() {
        if (readMethod == null) {
            Class<?> tmpClass = rootClass;
            StringTokenizer st = new StringTokenizer(memberName, ".");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                // 通过token找getter方法
                if (!st.hasMoreTokens()) {
                    // 找到最后的token，找到结果
                    readMethod = findReadMethod(tmpClass, token);
                    break;
                }
                tmpClass = getTokenType(token, tmpClass);
            }
        }
        return readMethod;
    }

    public Method getWriteMethod() {
        if (writeMethod == null) {
            Class<?> tmpClass = rootClass;
            StringTokenizer st = new StringTokenizer(memberName, ".");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                // 通过token找getter方法
                if (!st.hasMoreTokens()) {
                    // 找到最后的token，找到结果
                    writeMethod = findWriterMethod(tmpClass, token);
                    break;
                }
                tmpClass = getTokenType(token, tmpClass);
            }
        }
        return writeMethod;
    }

    protected Class<?> getTokenType(String token, Class<?> clazz) {
        // 通过token找字段
        Field f = findField(clazz, token);
        if (f != null) {
            // 使用field的类型设置接下去要查找的token
            return clazz = f.getType();
        }
        // 未找到通过getter方法查找
        Method m = findReadMethod(clazz, token);
        if (m != null) {
            return m.getReturnType();
        }
        // 通过setter方法查找
        m = findWriterMethod(clazz, token);
        if (m != null) {
            return m.getParameterTypes()[0];
        }
        throw new IllegalArgumentException(clazz + " no such member " + token);
    }

    protected Object getTokenValue(String token, Object target) {
        Method method = findReadMethod(target.getClass(), token);
        if (method != null) {
            return invokeMethod(method, target);
        }
        return getFieldValue(token, target);
    }

    protected void setTokenValue(String token, Object target, Object value) {
        Method method = findWriterMethod(target.getClass(), token);
        if (method != null) {
            invokeMethod(method, target, value);
        } else {
            setFieldValue(token, target, value);
        }
    }

    public Object get(Object root) {
        Assert.isTrue(isTarget(root), root + " not instance of " + rootClass);
        Assert.isTrue(isReadable(), rootClass + " " + memberName + " not readable");
        return get(memberName, root);
    }

    private Object get(String path, Object target) {
        StringTokenizer st = new StringTokenizer(memberName, ".");
        Object result = target;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            result = getTokenValue(token, result);
        }
        return result;
    }

    public void set(Object root, Object value) {
        Assert.isTrue(isTarget(root), root + " not instance of " + rootClass);
        Assert.isTrue(isWriteable(), rootClass + " " + memberName + " not writeable");
        // 获取$.user.name中的user
        int dotIndex = memberName.lastIndexOf(".");
        String lastToken;
        Object target;
        if (dotIndex > 0) {
            target = get(memberName.substring(dotIndex), root);
            lastToken = memberName.substring(dotIndex + 1, memberName.length());
        } else {
            target = root;
            lastToken = memberName;
        }
        setTokenValue(lastToken, target, value);
    }

    public boolean isReadable() {
        return getField() != null || getReadMethod() != null;
    }

    public boolean isWriteable() {
        return getField() != null || getWriteMethod() != null;
    }

    public boolean isTarget(Object target) {
        return target != null && rootClass.isInstance(target);
    }

    private static String getMethodSimpleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(4);
    }

}
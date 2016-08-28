package com.harmony.umbrella.access;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class MemberAccess {

    /**
     * 通过指定的字段名称或字段路径到达最终的字段member(该路径表达式使用{@code '.'}分割)
     * 
     * @param clazz
     *            root类
     * @param name
     *            字段的路径
     * @return member
     */
    public static Member access(Class<?> clazz, String name) {
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex < 0) {
            Field field = findField(clazz, name);
            if (field == null) {
                Method readMethod = findReadMethod(clazz, name);
                if (readMethod != null) {
                    return new PathMember(clazz, name, readMethod);
                }
                throw new IllegalArgumentException(clazz + " " + name + " not found");
            }
            return new PathMember(clazz, name, field);
        }
        String path = name.substring(0, lastDotIndex);
        String memberName = name.substring(lastDotIndex + 1);
        return new PathMember(clazz, path, memberName);
    }

    /**
     * 通过字段构建member
     * 
     * @param field
     * @return
     */
    public static Member access(Field field) {
        return access(field.getDeclaringClass(), field);
    }

    /**
     * 通过字段与目标类构建member,field必须包含在clazz中(clazz可以是field声明类的子类)。
     * 
     * @param clazz
     *            包含该field的类
     * @param field
     *            最终接触的field
     * @return member
     */
    public static Member access(Class<?> clazz, Field field) {
        if (!contains(clazz, field)) {
            throw new IllegalArgumentException(field + " not in " + clazz);
        }
        return new PathMember(clazz, field.getName(), field);
    }

    /**
     * @see #access(Class, Method)
     */
    public static Member access(Method readMethod) {
        return access(readMethod.getDeclaringClass(), readMethod);
    }

    /**
     * 通过getter方法构建member,method不洗包含在clazz中(clazz可以是method声明类的子类)
     * 
     * @param clazz
     *            包含该readMethod的类
     * @param readMethod
     *            最终解除的readMethod
     * @return
     */
    public static Member access(Class<?> clazz, Method readMethod) {
        if (!isReadMethod(readMethod)) {
            throw new IllegalArgumentException(readMethod + " not a getter method");
        }
        if (!contains(clazz, readMethod)) {
            throw new IllegalArgumentException(readMethod + " not in " + clazz);
        }
        return new PathMember(clazz, toFieldName(readMethod), readMethod);
    }

    public static boolean isReadable(Class<?> clazz, String name) {
        return findField(clazz, name) != null || findReadMethod(clazz, name) != null;
    }

    public static boolean isWriteable(Class<?> clazz, String name) {
        return findField(clazz, name) != null || findWriterMethod(clazz, name) != null;
    }

    static Object get(String name, Object val) {
        return ReflectionUtils.getFieldValue(name, val);
    }
    
    static Class<?> getPathType(Class<?> clazz, String name) {
        StringTokenizer st = new StringTokenizer(name, ".");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            Class<?> tmp = getTokenType(clazz, token);
            if (tmp == null) {
                throw new IllegalArgumentException(clazz + " no such member " + token);
            }
            clazz = tmp;
        }
        return clazz;
    }

    static Class<?> getTokenType(Class<?> clazz, String token) {
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
        return null;
    }

    static boolean contains(Class<?> clazz, Method method) {
        Class<?> methodOwnerType = method.getDeclaringClass();
        return ClassUtils.isAssignable(methodOwnerType, clazz);
    }

    static boolean contains(Class<?> clazz, Field field) {
        Class<?> fieldOwnerType = field.getDeclaringClass();
        return ClassUtils.isAssignable(fieldOwnerType, clazz);
    }

    static String toFieldName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(4);
    }

}
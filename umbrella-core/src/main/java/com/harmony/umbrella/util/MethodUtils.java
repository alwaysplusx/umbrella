/*
 * Copyright 2013-2015 wuxii@foxmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.util;

import static com.harmony.umbrella.util.FieldUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuxii@foxmail.com
 */
public abstract class MethodUtils {

    /**
     * Cache for {@link Class#getDeclaredMethods()}, allowing for fast
     * iteration.
     */
    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentHashMap<Class<?>, Method[]>(256);

    /**
     * 通过field查找对应的getter方法
     * 
     * @param source
     *            目标类
     * @param field
     *            字段
     * @return 字段对应的getter方法
     * @throws NoSuchMethodException
     *             如果未找到getter方法
     */
    public static Method findReadMethod(Class<?> source, Field field) throws NoSuchMethodException {
        Assert.notNull(field, "field must not be null");
        return findReadMethod(source, field.getName());
    }

    /**
     * 通过field名称查找对应的getter方法
     * 
     * @param source
     *            目标类
     * @param field
     *            字段名称
     * @return 字段对应的getter方法
     * @throws NoSuchMethodException
     *             如果未找到getter方法
     */
    public static Method findReadMethod(Class<?> source, String fieldName) throws NoSuchMethodException {
        Method method = findMethod(source, readMethodName(fieldName));
        if (method == null) {
            throw new NoSuchMethodException(fieldName);
        }
        return method;
    }

    /**
     * 通过field查找对应的setter方法
     * 
     * @param source
     *            目标类
     * @param field
     *            字段
     * @return 字段对应的setter方法
     * @throws NoSuchMethodException
     *             如果未找到getter方法
     */
    public static Method findWriterMethod(Class<?> source, Field field) throws NoSuchMethodException {
        Assert.notNull(field, "field must not be null");
        return findWriterMethod(source, field.getName());
    }

    /**
     * 通过field名称查找对应的setter方法
     * 
     * @param source
     *            目标类
     * @param field
     *            字段名称
     * @return 字段对应的setter方法
     * @throws NoSuchMethodException
     *             如果未找到getter方法
     */
    public static Method findWriterMethod(Class<?> source, String fieldName) throws NoSuchMethodException {
        Method method = findMethod(source, writerMethodName(fieldName));
        if (method == null) {
            throw new NoSuchMethodException(fieldName);
        }
        return findMethod(source, writerMethodName(fieldName));
    }

    /**
     * 在指定类以及父类中查找指定名称无参数的方法,
     * <p>
     * Attempt to find a {@link Method} on the supplied class with the supplied
     * name and no parameters. Searches all superclasses up to {@code Object}.
     * <p>
     * Returns {@code null} if no {@link Method} can be found.
     * 
     * @param source
     *            the class to introspect
     * @param name
     *            the name of the method
     * @return the Method object, or {@code null} if none found
     */
    public static Method findMethod(Class<?> source, String name) {
        return findMethod(source, name, new Class<?>[0]);
    }

    /**
     * 在指定类以及其父类中查找名称以及参数类型相同的的方法
     * <p>
     * Attempt to find a {@link Method} on the supplied class with the supplied
     * name and parameter types. Searches all superclasses up to {@code Object}.
     * <p>
     * Returns {@code null} if no {@link Method} can be found.
     * 
     * @param source
     *            the class to introspect
     * @param name
     *            the name of the method
     * @param paramTypes
     *            the parameter types of the method (may be {@code null} to
     *            indicate any signature)
     * @return the Method object, or {@code null} if none found
     */
    public static Method findMethod(Class<?> source, String name, Class<?>... paramTypes) {
        Class<?> searchType = source;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            for (Method method : methods) {
                if (name.equals(method.getName()) && ClassUtils.isAssignable(method.getParameterTypes(), paramTypes)) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 根据参数类型过滤出source中符合参数类型的方法
     * <p>
     * 过滤出的方法参数可能为期待的参数类型的父类，但绝不能为子类
     * 
     * @param source
     * @param parameterTypes
     *            期待的参数类型
     * @return
     */
    public static Method[] findMethods(Class<?> source, Class<?>[] parameterTypes) {
        List<Method> result = new LinkedList<Method>();
        Class<?> searchType = source;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            for (Method method : methods) {
                if (ClassUtils.isAssignable(method.getParameterTypes(), parameterTypes)) {
                    result.add(method);
                }
            }
            searchType = searchType.getSuperclass();
        }
        return result.toArray(new Method[result.size()]);
    }

    /**
     * 过滤符合方法名，参数类型，返回类型相同的方法.
     * <p>
     * 过滤出的方法参数可能为期待的参数类型的父类，但绝不能为子类
     * 
     * @param source
     * @param methodName
     *            期待的方法名
     * @param parameterTypes
     *            期待的参数类型
     * @param returnType
     *            期待的返回类型
     * @return
     */
    public static Method findMethod(Class<?> source, String methodName, Class<?>[] parameterTypes, Class<?> returnType) {
        Method method = findMethod(source, methodName, parameterTypes);
        if (method != null && ClassUtils.isAssignable(returnType, method.getReturnType())) {
            return method;
        }
        return null;
    }

    /**
     * 通过MethodFilter查找对应的方法
     * 
     * @param source
     *            目标类
     * @param mf
     *            方法过滤器
     * @return 目标方法, 如果未找到返回null
     */
    public static Method findMethod(Class<?> source, MethodFilter mf) {
        Class<?> searchType = source;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            for (Method method : methods) {
                if (mf.matches(method)) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static Object invokeMethod(String methodName, Object target) throws NoSuchMethodException {
        Method method = findMethod(target.getClass(), methodName);
        if (method == null) {
            throw new NoSuchMethodException(methodName);
        }
        return invokeMethod(method, target);
    }

    /**
     * 通过方法名与方法参数执行目标的方法
     * 
     * @param methodName
     *            方法名称
     * @param target
     *            待执行的目标类
     * @param args
     *            方法参数
     * @return 方法调用结果
     */
    public static Object invokeMethod(String methodName, Object target, Object... args) throws NoSuchMethodException {
        Method method = findMethod(target.getClass(), methodName, ClassUtils.toTypeArray(args));
        if (method == null) {
            throw new NoSuchMethodException(methodName);
        }
        return invokeMethod(method, target, args);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object
     * with no arguments. The target object can be {@code null} when invoking a
     * static {@link Method}.
     * <p>
     * Thrown exceptions are handled via a call to
     * {@link #handleReflectionException}.
     * 
     * @param method
     *            the method to invoke
     * @param target
     *            the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, new Object[0]);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object
     * with the supplied arguments. The target object can be {@code null} when
     * invoking a static {@link Method}.
     * <p>
     * Thrown exceptions are handled via a call to
     * {@link #handleReflectionException}.
     * 
     * @param method
     *            the method to invoke
     * @param target
     *            the target object to invoke the method on
     * @param args
     *            the invocation arguments (may be {@code null})
     * @return the invocation result, if any
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        Assert.notNull(method, "method must not be null");
        Assert.notNull(target, "target must not be null");
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            ReflectionUtils.handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * Invoke the specified JDBC API {@link Method} against the supplied target
     * object with no arguments.
     * 
     * @param method
     *            the method to invoke
     * @param target
     *            the target object to invoke the method on
     * @return the invocation result, if any
     * @throws SQLException
     *             the JDBC API SQLException to rethrow (if any)
     * @see #invokeJdbcMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeJdbcMethod(Method method, Object target) throws SQLException {
        return invokeJdbcMethod(method, target, new Object[0]);
    }

    /**
     * Invoke the specified JDBC API {@link Method} against the supplied target
     * object with the supplied arguments.
     * 
     * @param method
     *            the method to invoke
     * @param target
     *            the target object to invoke the method on
     * @param args
     *            the invocation arguments (may be {@code null})
     * @return the invocation result, if any
     * @throws SQLException
     *             the JDBC API SQLException to rethrow (if any)
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeJdbcMethod(Method method, Object target, Object... args) throws SQLException {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof SQLException) {
                throw (SQLException) ex.getTargetException();
            }
            ReflectionUtils.handleInvocationTargetException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * Perform the given callback operation on all matching methods of the given
     * class and superclasses.
     * <p>
     * The same named method occurring on subclass and superclass will appear
     * twice, unless excluded by a {@link MethodFilter}.
     * 
     * @param clazz
     *            the class to introspect
     * @param mc
     *            the callback to invoke for each method
     * @see #doWithMethods(Class, MethodCallback, MethodFilter)
     */
    public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
        doWithMethods(clazz, mc, null);
    }

    /**
     * Perform the given callback operation on all matching methods of the given
     * class and superclasses (or given interface and super-interfaces).
     * <p>
     * The same named method occurring on subclass and superclass will appear
     * twice, unless excluded by the specified {@link MethodFilter}.
     * 
     * @param clazz
     *            the class to introspect
     * @param mc
     *            the callback to invoke for each method
     * @param mf
     *            the filter that determines the methods to apply the callback
     *            to
     */
    public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf) {
        // Keep backing up the inheritance hierarchy.
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            if (mf != null && !mf.matches(method)) {
                continue;
            }
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
        if (clazz.getSuperclass() != null) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        } else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    /**
     * Get all declared methods on the leaf class and all superclasses. Leaf
     * class methods are included first.
     * 
     * @param leafClass
     *            the class to introspect
     */
    public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
        final List<Method> methods = new ArrayList<Method>(32);
        doWithMethods(leafClass, new MethodCallback() {
            @Override
            public void doWith(Method method) {
                methods.add(method);
            }
        });
        return methods.toArray(new Method[methods.size()]);
    }

    /**
     * This variant retrieves {@link Class#getDeclaredMethods()} from a local
     * cache in order to avoid the JVM's SecurityManager check and defensive
     * array copying.
     */
    private static Method[] getDeclaredMethods(Class<?> clazz) {
        Method[] result = declaredMethodsCache.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredMethods();
            declaredMethodsCache.put(clazz, result);
        }
        return result;
    }

    /**
     * Action to take on each method.
     */
    public interface MethodCallback {

        /**
         * Perform an operation using the given method.
         * 
         * @param method
         *            the method to operate on
         */
        void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
    }

    /**
     * Callback optionally used to filter methods to be operated on by a method
     * callback.
     */
    public interface MethodFilter {

        /**
         * Determine whether the given method matches.
         * 
         * @param method
         *            the method to check
         */
        boolean matches(Method method);
    }
}

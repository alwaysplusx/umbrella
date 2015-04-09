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
package com.harmony.modules.utils;

import static com.harmony.modules.utils.ClassUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 反射调用方法
 * @author wuxii@foxmail.com
 */
public abstract class MethodCaller {

    private final static Object[] EMPTY_PARAMETER = new Object[0];
    @SuppressWarnings("unused")
    private final static Class<?>[] EMPTY_PARAMETER_TYPES = new Class[0];

    /**
     * 指定方法名称以及参数，反射调用对应target的该方法
     * @param target
     * @param methodName
     * @param args
     * @return
     * @throws NoSuchMethodException 指定方法名并参数类型与方法不匹配
     * @throws IllegalArgumentException 参数不匹配
     * @throws IllegalAccessException 
     * @throws InvocationTargetException
     */
    public static Object invokeMethod(Object target, String methodName, Object... args) throws NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Method method = getRelativeMethod(target.getClass(), methodName, args);
        return method.invoke(target, args);
    }

    /**
     * 反射执行指定方法
     * @param target
     * @param method
     * @param args
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokeMethod(Object target, Method method, Object... args) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        return method.invoke(target, args);
    }

    /**
     * 执行方法，如果有异常则将检查异常转为非检查异常抛出
     * @param target
     * @param method
     * @param args
     * @return
     */
    public static Object invokeMethodWithUncheckException(Object target, Method method, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 使用反射获取字段值，只尝试使用字段的getter方法
     * @param target
     * @param field
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokeFieldGetMethod(Object target, Field field) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        if (field == null) {
            throw new NullPointerException("field is null");
        }
        String getMethodName = toGetMethodName(field.getName());
        Method method = getRelativeMethod(target.getClass(), getMethodName, EMPTY_PARAMETER);
        return method.invoke(target);
    }

    /**
     * 使用反射回去字段的值，只调用的是getter方法
     * @param target
     * @param fieldName
     * @return
     */
    public static Object invokeGetMethodWithUncheckException(Object target, String fieldName) {
        String getMethodName = toGetMethodName(fieldName);
        try {
            Method method = getRelativeMethod(target.getClass(), getMethodName, EMPTY_PARAMETER);
            return method.invoke(target);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 使用反射设置字段的值，只调用setter方法
     * @param target
     * @param fieldName
     * @param value
     */
    public static void invokeSetMethodWithUncheckExeption(Object target, String fieldName, Object value) {
        String setMethodName = toSetMethodName(fieldName);
        try {
            Method method = getRelativeMethod(target.getClass(), setMethodName, EMPTY_PARAMETER);
            method.invoke(target, value);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    private static Method getRelativeMethod(Class<?> targetClass, String methodName, Object[] args) throws NoSuchMethodException {
        Class<?> clazz = ClassUtils.getRealClass(targetClass);
        return MethodMatcher.filterMethod(clazz, methodName, MethodMatcher.toParameterTypes(args));
    }

    /**
     * 将字段名转为getter的方法名
     * @param fieldName
     * @return
     */
    public static String toGetMethodName(String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            throw new IllegalArgumentException("field name is empty");
        }
        return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    /**
     * 将字段名转为setter的方法名
     * @param fieldName
     * @return
     */
    public static String toSetMethodName(String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            throw new IllegalArgumentException("field name is empty");
        }
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    /**
     * 方法匹配过滤. 匹配{@linkplain java.lang.Class#getMethods()}
     */
    public static abstract class MethodMatcher {

        /**
         * 根据方法名过滤出source中符合方法名的方法
         * 
         * @param source 目表类
         * @param methodName 方法名
         * @return 如果没有符合条件的方法则返回null
         */
        public static Method[] filterMethod(Class<?> source, String methodName) {
            List<Method> result = new LinkedList<Method>();
            for (Method method : source.getMethods()) {
                if (Object.class == method.getDeclaringClass())
                    continue;
                if (method.getName().equals(method)) {
                    result.add(method);
                }
            }
            return result.toArray(new Method[result.size()]);
        }

        /**
         * 根据参数类型过滤出source中符合参数类型的方法<p>过滤出的方法参数可能为期待的参数类型的父类，但绝不能为子类
         * 
         * @param source
         * @param parameterTypes 期待的参数类型
         * @return
         */
        public static Method[] filterMethod(Class<?> source, Class<?>[] parameterTypes) {
            List<Method> result = new LinkedList<Method>();
            for (Method method : source.getMethods()) {
                if (Object.class == method.getDeclaringClass())
                    continue;
                Class<?>[] types = method.getParameterTypes();
                if (types.length == parameterTypes.length) {
                    int i, max;
                    for (i = 0, max = types.length; i < max; i++) {
                        if (!isAssignableIgnoreClassLoader(types[i], parameterTypes[i]))
                            continue;
                    }
                    if (i == max)
                        result.add(method);
                }
            }
            return result.toArray(new Method[result.size()]);
        }

        /**
         * 过滤符合方法名称和参数类型的方法<p>过滤出的方法参数可能为期待的参数类型的父类，但绝不能为子类
         * @param source
         * @param methodName 期待方法名
         * @param parameterTypes 期待参数类型
         * @return
         */
        public static Method filterMethod(Class<?> source, String methodName, Class<?>[] parameterTypes) {
            Method result = null;
            for (Method method : source.getMethods()) {
                if (Object.class == method.getDeclaringClass())
                    continue;
                if (method.getName().equals(methodName)) {
                    Class<?>[] types = method.getParameterTypes();
                    if (types.length == parameterTypes.length) {
                        int i, max;
                        for (i = 0, max = types.length; i < max; i++) {
                            if (!isAssignableIgnoreClassLoader(types[i], parameterTypes[i]))
                                continue;
                        }
                        if (i == max) {
                            result = method;
                            break;
                        }
                    }
                }
            }
            return result;
        }

        /**
         * 过滤符合方法名，参数类型，返回类型相同的方法.
         * <p>过滤出的方法参数可能为期待的参数类型的父类，但绝不能为子类
         * @param source
         * @param methodName 期待的方法名
         * @param parameterTypes 期待的参数类型
         * @param returnType 期待的返回类型
         * @return
         */
        public static Method filterMethod(Class<?> source, String methodName, Class<?>[] parameterTypes, Class<?> returnType) {
            Method result = null;
            for (Method method : source.getMethods()) {
                if (Object.class == method.getDeclaringClass())
                    continue;
                if (method.getName().equals(methodName)) {
                    Class<?>[] types = method.getParameterTypes();
                    if (types.length == parameterTypes.length) {
                        int i, max;
                        for (i = 0, max = types.length; i < max; i++) {
                            if (!isAssignableIgnoreClassLoader(types[i], parameterTypes[i]))
                                continue;
                        }
                        if (i == max && isAssignableIgnoreClassLoader(returnType, method.getReturnType())) {
                            result = method;
                            break;
                        }
                    }
                }
            }
            return result;
        }

        /**
         * 过滤目标类中符合期待方法的方法
         * <p>符合的方法主要为：
         * <ul>
         *  <li>参数相同</li>
         *  <li>参数相同并且方法名相同</li>
         * </ul>
         * @param source
         * @param exceptMethod
         * @return
         */
        public static Method[] filterMethod(Class<?> source, Method exceptMethod) {
            return filterMethod(source, exceptMethod.getParameterTypes());
        }

        /**
         * 检测m2的参数是否符合m1
         * @param m1
         * @param m2
         * @return
         */
        public static boolean parameterTypeMatchers(Method m1, Method m2) {
            if (m1 == m2)
                return true;
            return typeEquals(m1.getParameterTypes(), m2.getParameterTypes());
        }

        /**
         * 将请求参数转化为Class类型数组，对应于{@linkplain Method#getParameterTypes()}
         * @param args
         * @return
         * @see Method#getParameterTypes()
         */
        public static Class<?>[] toParameterTypes(Object[] args) {
            Class<?>[] parameterTypes = new Class<?>[0];
            if (args != null && args.length > 0) {
                parameterTypes = new Class[args.length];
                for (int i = 0, max = args.length; i < max; i++) {
                    if (args[i] != null)
                        parameterTypes[i] = getRealClass(args[i].getClass());
                    else
                        parameterTypes[i] = Object.class;
                }
            }
            return parameterTypes;
        }

    }

    /**
     * 方法过滤
     */
    public interface MethodFilter {

        boolean accept(Method method);

    }
}

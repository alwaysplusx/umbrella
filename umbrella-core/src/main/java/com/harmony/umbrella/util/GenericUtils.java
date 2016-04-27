/*
 * Copyright 2002-2014 the original author or authors.
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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型工具类
 * 
 * @author wuxii@foxmail.com
 */
public class GenericUtils {

    /**
     * 获取字段指定index的泛型
     * 
     * @param field
     *            字段
     * @param index
     *            泛型的index
     * @return
     */
    public static Class<?> getFieldGeneric(Field field, int index) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            Type genericType = actualTypeArguments[index];
            if (genericType instanceof Class) {
                return (Class<?>) genericType;
            }
            throw new IllegalArgumentException(field.getName() + "[" + index + "]=" + genericType + " not class");
        }
        throw new IllegalArgumentException(field.getName() + " not have generic");
    }

    public static boolean isJavaTypeGeneric(Field field, int index) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            Type genericType = actualTypeArguments[index];
            if (genericType instanceof Class) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前类的父类的指定index有效泛型
     * 
     * @param clazz
     *            被查找的类
     * @param index
     *            泛型的index
     * @return 有效泛型
     * @throws IllegalArgumentException
     *             泛型未找到或者不是有效的泛型
     */
    public static Class<?> getSuperGeneric(Class<?> clazz, int index) {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null || superclass == Object.class) {
            throw new IllegalArgumentException(clazz + "父类不包含任何父类");
        }
        return getTargetGeneric(clazz, superclass, index);
    }

    /**
     * 获取类clazz的有继承或实现关系的类target的泛型
     * 
     * @param clazz
     * @param target
     * @return
     */
    public static Class<?> getTargetGeneric(Class<?> clazz, Class<?> target, int index) {
        if (!target.isAssignableFrom(clazz) || clazz == target) {
            throw new IllegalArgumentException("指定的target与原类型无继承或实现关系");
        }
        if (target == Object.class) {
            throw new IllegalArgumentException("target class is Object.class");
        }
        if (target.isInterface()) {
            return getInterfaceTargetGeneric(clazz, target, index);
        }
        return getClassTargetGeneric(clazz, target, index);
    }

    private static Class<?> getInterfaceTargetGeneric(Class<?> clazz, Class<?> target, int index) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (genericInterfaces != null && genericInterfaces.length > 0) {
            for (Type genericType : genericInterfaces) {
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType ptype = (ParameterizedType) genericType;
                    if (ptype.getRawType() == target) {
                        // 找到了匹配的目标类型
                        Type result = ptype.getActualTypeArguments()[index];
                        if (result instanceof Class) {
                            return (Class<?>) result;
                        }
                        throw new IllegalArgumentException(result + "不是有效的泛型类型");
                    }
                }
            }
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> cls : interfaces) {
            Class<?> result = getInterfaceTargetGeneric(cls, target, index);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static Class<?> getClassTargetGeneric(Class<?> clazz, Class<?> target, int index) {
        // 父类的泛型type
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass != null) {
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) genericSuperclass;
                if (ptype.getRawType() == target) {
                    // 找到了匹配的目标类型
                    Type result = ptype.getActualTypeArguments()[index];
                    if (result instanceof Class) {
                        return (Class<?>) result;
                    }
                    throw new IllegalArgumentException(result + "不是有效的泛型类型");
                }
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != Object.class) {
            return getClassTargetGeneric(clazz.getSuperclass(), target, index);
        }
        return null;
    }

}

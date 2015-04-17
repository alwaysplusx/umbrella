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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Miscellaneous class utility methods. Mainly for internal use within the
 * framework.
 *
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Sam Brannen
 * @since 1.1
 * @see TypeUtils
 * @see ReflectionUtils
 */
public abstract class ClassUtils {

    /** Suffix for array class names: "[]" */
    public static final String ARRAY_SUFFIX = "[]";

    /** The package separator character '.' */
    private static final char PACKAGE_SEPARATOR = '.';

    /** The path separator character '/' */
    private static final char PATH_SEPARATOR = '/';

    /** The CGLIB class separator character "$$" */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    /** The ".class" file suffix */
    public static final String CLASS_FILE_SUFFIX = ".class";
    /**
     * Map with primitive wrapper type as key and corresponding primitive type
     * as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);
    /**
     * Map with primitive type as key and corresponding wrapper type as value,
     * for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap
                // ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the
                    // caller can live with null...
                }
            }
        }
        return cl;
    }

    /**
     * Given an input class object, return a string which consists of the
     * class's package name as a pathname, i.e., all dots ('.') are replaced by
     * slashes ('/'). Neither a leading nor trailing slash is added. The result
     * could be concatenated with a slash and the name of a resource and fed
     * directly to {@code ClassLoader.getResource()}. For it to be fed to
     * {@code Class.getResource} instead, a leading slash would also have to be
     * prepended to the returned value.
     * 
     * @param clazz
     *            the input class. A {@code null} value or the default (empty)
     *            package will result in an empty string ("") being returned.
     * @return a path which represents the package name
     * @see ClassLoader#getResource
     * @see Class#getResource
     */
    public static String classPackageAsResourcePath(Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        String className = clazz.getName();
        int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        if (packageEndIndex == -1) {
            return "";
        }
        String packageName = className.substring(0, packageEndIndex);
        return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }

    /**
     * 比较两个类是否相同
     * 
     * @param c1
     * @param c2
     * @return
     */
    public static boolean equals(Class<?> c1, Class<?> c2) {
        return c1 == c2;
    }

    /**
     * 比较两个类的全显定名称是否相同
     * 
     * @param c1
     * @param c2
     * @return
     */
    public static boolean equalsIgnoreClassLoader(Class<?> c1, Class<?> c2) {
        return c1.getCanonicalName().equals(c2.getCanonicalName());
    }

    /**
     * subClass是否是superClass的子类
     * 
     * @param superClass
     * @param subClass
     * @return
     */
    public static boolean isAssignable(Class<?> superClass, Class<?> subClass) {
        if (equals(superClass, subClass))
            return true;
        if (superClass == null && subClass == null)
            return true;
        if (subClass == null || superClass == null)
            return false;
        if (superClass.isAssignableFrom(subClass)) {
            return true;
        }
        if (superClass.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(subClass);
            if (resolvedPrimitive != null && superClass.equals(resolvedPrimitive)) {
                return true;
            }
        } else {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(subClass);
            if (resolvedWrapper != null && superClass.isAssignableFrom(resolvedWrapper)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the given type is assignable from the given value, assuming
     * setting by reflection. Considers primitive wrapper classes as assignable
     * to the corresponding primitive types.
     * 
     * @param type
     *            the target type
     * @param value
     *            the value that should be assigned to the type
     * @return if the type is assignable from the value
     */
    public static boolean isAssignableValue(Class<?> type, Object value) {
        return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
    }

    /**
     * 检查输入类型是否符合模版的参数
     * 
     * @param pattern
     *            参数的模版
     * @param inputTypes
     *            输入类型
     * @return
     * @see ClassUtils#isAssignable(Class, Class)
     */
    public static boolean typeEquals(Class<?>[] pattern, Class<?>[] inputTypes) {
        if (pattern.length != inputTypes.length)
            return false;
        for (int i = 0, max = pattern.length; i < max; i++) {
            if (!isAssignable(inputTypes[i], pattern[i]))
                return false;
        }
        return true;
    }

    /**
     * 检查输入参数是否符合模版的参数类型
     * 
     * @param pattern
     *            模版参数
     * @param args
     *            输入参数
     * @return
     */
    public static boolean typeEquals(Class<?>[] pattern, Object[] args) {
        return typeEquals(pattern, toTypeArray(args));
    }

    /**
     * 将参数转化为Class类型数组，对应于{@linkplain Method#getParameterTypes()}
     * 
     * @param args
     * @return
     * @see Method#getParameterTypes()
     */
    public static Class<?>[] toTypeArray(Object[] args) {
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

    /**
     * Replacement for {@code Class.forName()} that also returns Class instances
     * for primitives (e.g. "int") and array class names (e.g. "String[]").
     * Furthermore, it is also capable of resolving inner class names in Java
     * source style (e.g. "java.lang.Thread.State" instead of
     * "java.lang.Thread$State").
     * 
     * @param name
     *            the name of the Class
     * @param classLoader
     *            the class loader to use (may be {@code null}, which indicates
     *            the default class loader)
     * @return Class instance for the supplied name
     * @throws ClassNotFoundException
     *             if the class was not found
     * @throws LinkageError
     *             if the class file could not be loaded
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        return Class.forName(name);
    }

    /**
     * 获得clazz的真实类型
     * 
     * @param clazz
     * @return
     */
    static Class<?> getRealClass(Class<?> clazz) {
        // if (Proxy.isProxyClass(clazz)) {
        // }
        return clazz;
    }

    /**
     * Check whether the specified class is a CGLIB-generated class.
     * 
     * @param clazz
     *            the class to check
     */
    public static boolean isCglibProxyClass(Class<?> clazz) {
        return (clazz != null && isCglibProxyClassName(clazz.getName()));
    }

    /**
     * Check whether the specified class name is a CGLIB-generated class.
     * 
     * @param className
     *            the class name to check
     */
    public static boolean isCglibProxyClassName(String className) {
        return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
    }
}

package com.harmony.umbrella.util;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Miscellaneous class utility methods. Mainly for internal use within the
 * framework.
 *
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Sam Brannen
 * @see ReflectionUtils
 * @since 1.1
 */
public class ClassUtils {

    /**
     * Suffix for array class names: "[]"
     */
    public static final String ARRAY_SUFFIX = "[]";

    /**
     * The package separator character '.'
     */
    private static final char PACKAGE_SEPARATOR = '.';

    /**
     * The path separator character '/'
     */
    private static final char PATH_SEPARATOR = '/';

    /**
     * The CGLIB class separator character "$$"
     */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * The ".class" file suffix
     */
    public static final String CLASS_FILE_SUFFIX = ".class";
    /**
     * Map with primitive wrapper type as key and corresponding primitive type
     * as value, for example: Integer.class -> int.class.
     */
    static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);
    /**
     * Map with primitive type as key and corresponding wrapper type as value,
     * for example: int.class -> Integer.class.
     */
    static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);

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

    /**
     * 元类型的包装类
     * 
     * @param primitiveType
     *            元数据类型
     * @return 包装类类型
     */
    public static Class<?> getPrimitiveWrapperType(Class<?> primitiveType) {
        return primitiveTypeToWrapperMap.get(primitiveType);
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
     * 比较两个类的全显定名称是否相同
     *
     * @param c1
     * @param c2
     * @return
     */
    public static boolean canonicalNameEquals(Class<?> c1, Class<?> c2) {
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1 == c2) {
            return true;
        }
        return c1.getCanonicalName().equals(c2.getCanonicalName());
    }

    /**
     * subClass是否是superClass的子类
     *
     * @param superClass
     *            待比较父类
     * @param subClass
     *            待比较子类
     */
    public static boolean isAssignable(Class<?> superClass, Class<?> subClass) {
        if (superClass == subClass) {
            return true;
        }
        if (subClass == null || superClass == null) {
            return false;
        }
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
     * 检查输入类型是否符合模版的参数. 首先类型数据的长度匹配, 再对各个类型进行
     * {@linkplain #isAssignable(Class, Class)}匹配
     *
     * @param pattern
     *            参数的模版
     * @param inputTypes
     *            输入类型
     * @return true 所有类型都匹配
     * @see ClassUtils#isAssignable(Class, Class)
     */
    public static boolean isAssignable(Class<?>[] pattern, Class<?>[] inputTypes) {
        if (pattern.length != inputTypes.length)
            return false;
        for (int i = 0, max = pattern.length; i < max; i++) {
            if (!isAssignable(inputTypes[i], pattern[i]))
                return false;
        }
        return true;
    }

    /**
     * 将参数转化为Class类型数组，对应于{@linkplain Method#getParameterTypes()}
     * <p/>
     * 如果args中存在{@code null}的元素, 则将对象的类映射为{@linkplain Object}
     *
     * @param args
     *            参数数组
     * @return 类型数组
     * @see Method#getParameterTypes()
     */
    public static Class<?>[] toTypeArray(Object[] args) {
        if (args == null || args.length == 0) {
            return new Class<?>[0];
        }
        Class<?>[] parameterTypes = new Class[args.length];
        if (args != null && args.length > 0) {
            for (int i = 0, max = args.length; i < max; i++) {
                if (args[i] != null) {
                    parameterTypes[i] = getRealClass(args[i].getClass());
                } else {
                    parameterTypes[i] = Object.class;
                }
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
    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(name, true, classLoader);
    }

    /**
     * for name，use default {@linkplain #getDefaultClassLoader() classLoader}
     *
     * @param name
     *            类名
     * @return 类
     * @throws ClassNotFoundException
     * @see {@link #forName(String, ClassLoader)}
     */
    public static Class<?> forName(String name) throws ClassNotFoundException {
        return forName(name, getDefaultClassLoader());
    }

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

    /**
     * 获得clazz的所有接口.包括递归父类的接口. 以及递归实现类的接口
     *
     * @param clazz
     * @return
     */
    public static Class<?>[] getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> result = new HashSet<Class<?>>();
        if (clazz.isInterface()) {
            result.add(clazz);
        }
        for (Class<?> claz : clazz.getInterfaces()) {
            Collections.addAll(result, getAllInterfaces(claz));
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            Collections.addAll(result, getAllInterfaces(clazz.getSuperclass()));
        }
        return result.toArray(new Class<?>[result.size()]);
    }

}

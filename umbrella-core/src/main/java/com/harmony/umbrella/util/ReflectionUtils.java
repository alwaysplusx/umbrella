package com.harmony.umbrella.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple utility class for working with the reflection API and handling
 * reflection exceptions.
 *
 * <p>
 * Only intended for internal use.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Costin Leau
 * @author Sam Brannen
 * @author Chris Beams
 * @since 1.2.2
 */
public class ReflectionUtils {

    /**
     * Cache for {@link Class#getDeclaredMethods()}, allowing for fast
     * iteration.
     */
    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentHashMap<Class<?>, Method[]>(256);

    /**
     * 通过field名称查找对应的getter方法
     * 
     * @param source
     *            目标类
     * @param field
     *            字段名称
     * @return 字段对应的getter方法
     */
    public static Method findReadMethod(Class<?> source, String fieldName) {
        String[] readMethodName = readMethodName(fieldName);
        for (String name : readMethodName) {
            Method method = findMethod(source, name);
            if (method != null) {
                return method;
            }
        }
        return null;
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
    public static Method findWriterMethod(Class<?> source, String fieldName) {
        return findMethod(source, writerMethodName(fieldName), new Class[] { Object.class });
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
     * <p>
     * 查找Class中符合指定名称的字段(在所有字段以及其所有父类的字段中查询:
     * {@linkplain Class#getDeclaredFields()})
     * 
     * @param clazz
     *            the class to introspect
     * @param name
     *            the name of the field
     * @return the corresponding Field object, or {@code null} if not found
     */
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /**
     * 查找符合名称或者类型的字段, name type不能同时为空
     * 
     * @param clazz
     *            the class to introspect
     * @param name
     *            the name of the field (may be {@code null} if type is
     *            specified)
     * @param type
     *            the type of the field (may be {@code null} if name is
     *            specified)
     * @return the corresponding Field object, or {@code null} if not found
     */
    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static Field findField(Class<?> clazz, FieldFilter ff) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(ff, "FieldFilter must not be null");
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if (ff.matches(field)) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static void setFieldValue(String fieldName, Object target, Object value) {
        Assert.notNull(fieldName, "fieldName not allow null");
        Assert.notNull(target, "target not allow null");
        // set in method way
        Method writeMethod = findWriterMethod(target.getClass(), fieldName);
        if (writeMethod != null) {
            invokeMethod(writeMethod, target, value);
            return;
        }
        // set in field way
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            throw new IllegalArgumentException(target + ", " + fieldName + " field not find");
        }
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }

    }

    /**
     * 设置字段值
     * <p>
     * Set the field represented by the supplied {@link Field field object} on
     * the specified {@link Object target object} to the specified {@code value}
     * . In accordance with {@link Field#set(Object, Object)} semantics, the new
     * value is automatically unwrapped if the underlying field has a primitive
     * type.
     * <p>
     * Thrown exceptions are handled via a call to
     * {@link #handleReflectionException(Exception)}.
     * 
     * @param field
     *            the field to set
     * @param target
     *            the target object on which to set the field
     * @param value
     *            the value to set; may be {@code null}
     */
    public static void setFieldValue(Field field, Object target, Object value) {
        Method writerMethod = findWriterMethod(target.getClass(), field.getName());
        if (writerMethod != null) {
            invokeMethod(writerMethod, target, value);
            return;
        }
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on
     * the specified {@link Object target object}. In accordance with
     * {@link Field#get(Object)} semantics, the returned value is automatically
     * wrapped if the underlying field has a primitive type.
     * <p>
     * Thrown exceptions are handled via a call to
     * {@link #handleReflectionException(Exception)}.
     * 
     * @param field
     *            the field to get
     * @param target
     *            the target object from which to get the field
     * @return the field's current value
     */
    public static Object getField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    public static Object getFieldValue(String fieldName, Object target) {
        Assert.notNull(target, "target not allow null");
        Assert.notNull(fieldName, "fieldName not allow null");

        Class<?> targetClass = target.getClass();
        Method readMethod = findReadMethod(targetClass, fieldName);

        if (readMethod != null) {
            return invokeMethod(readMethod, target);
        }

        Field field = findField(targetClass, fieldName);
        if (field == null) {
            throw new IllegalArgumentException(fieldName + " field not find");
        }
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(target);
        } catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * 获取字段值
     * <p>
     * Get the field represented by the supplied {@link Field field object} on
     * the specified {@link Object target object}. In accordance with
     * {@link Field#get(Object)} semantics, the returned value is automatically
     * wrapped if the underlying field has a primitive type.
     * <p>
     * Thrown exceptions are handled via a call to
     * {@link #handleReflectionException(Exception)}.
     * 
     * @param field
     *            the field to get
     * @param target
     *            the target object from which to get the field
     * @return the field's current value
     */
    public static Object getFieldValue(Field field, Object target) {
        Assert.notNull(field, "field not allow null");
        Class<?> targetClass = (target == null) ? field.getType() : target.getClass();
        Method readMethod = findReadMethod(targetClass, field.getName());
        if (readMethod != null) {
            return invokeMethod(readMethod, target);
        }
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(target);
        } catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * 将字段名转为getter的方法名
     * 
     * @param fieldName
     * @return
     */
    static String[] readMethodName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("field name is blank");
        }
        String name = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return new String[] { "get" + name, "is" + name };
    }

    /**
     * 将字段名转为setter的方法名
     * 
     * @param fieldName
     * @return
     */
    static String writerMethodName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("field name is blank");
        }
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    /**
     * 通过类名实例化对象
     * 
     * @param className
     *            待实例化的类名
     * @return 实例对象
     * @throws ClassNotFoundException
     *             if class name not found
     */
    public static Object instantiateClass(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className, false, ClassUtils.getDefaultClassLoader());
        return instantiateClass(clazz);
    }

    /**
     * 实例化对象
     * 
     * @param clazz
     *            待实例化的对象
     * @return 实例对象
     */
    public static <T> T instantiateClass(Class<T> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("");
        }
    }

    /**
     * Convenience method to instantiate a class using the given constructor. As
     * this method doesn't try to load classes by name, it should avoid
     * class-loading issues.
     * <p>
     * Note that this method tries to set the constructor accessible if given a
     * non-accessible (that is, non-public) constructor.
     * 
     * @param ctor
     *            the constructor to instantiate
     * @param args
     *            the constructor arguments to apply
     * @return the new instance
     */
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) {
        Assert.notNull(ctor, "Constructor must not be null");
        ReflectionUtils.makeAccessible(ctor);
        try {
            return ctor.newInstance(args);
        } catch (InvocationTargetException ex) {
            handleInvocationTargetException(ex);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException();
    }

    /**
     * Handle the given reflection exception. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>
     * Throws the underlying RuntimeException or Error in case of an
     * InvocationTargetException with such a root cause. Throws an
     * IllegalStateException with an appropriate message else.
     * 
     * @param ex
     *            the reflection exception to handle
     */
    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Handle the given invocation target exception. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>
     * Throws the underlying RuntimeException or Error in case of such a root
     * cause. Throws an IllegalStateException else.
     * 
     * @param ex
     *            the invocation target exception to handle
     */
    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}. Should
     * only be called if no checked exception is expected to be thrown by the
     * target method.
     * <p>
     * Rethrows the underlying exception cast to an {@link RuntimeException} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     * 
     * @param ex
     *            the exception to rethrow
     * @throws RuntimeException
     *             the rethrown exception
     */
    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}. Should
     * only be called if no checked exception is expected to be thrown by the
     * target method.
     * <p>
     * Rethrows the underlying exception cast to an {@link Exception} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     * 
     * @param ex
     *            the exception to rethrow
     * @throws Exception
     *             the rethrown exception (in case of a checked exception)
     */
    public static void rethrowException(Throwable ex) throws Exception {
        if (ex instanceof Exception) {
            throw (Exception) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Determine whether the given method explicitly declares the given
     * exception or one of its superclasses, which means that an exception of
     * that type can be propagated as-is within a reflective invocation.
     * 
     * @param method
     *            the declaring method
     * @param exceptionType
     *            the exception to throw
     * @return {@code true} if the exception can be thrown as-is; {@code false}
     *         if it needs to be wrapped
     */
    public static boolean declaresException(Method method, Class<?> exceptionType) {
        Class<?>[] declaredExceptions = method.getExceptionTypes();
        for (Class<?> declaredException : declaredExceptions) {
            if (declaredException.isAssignableFrom(exceptionType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReadMethod(Method method) {
        String methodName = method.getName();
        return method.getParameterTypes().length == 0
                && ((methodName.length() > 3 && methodName.startsWith("get")) || (methodName.length() > 2 && methodName.startsWith("is")));
    }

    public static boolean isWriteMethod(Method method) {
        String methodName = method.getName();
        return methodName.length() > 3//
                && methodName.startsWith("set") //
                && method.getReturnType() != void.class //
                && method.getParameterTypes().length == 1;
    }

    /**
     * Determine whether the given field is a "public static final" constant.
     * 
     * @param field
     *            the field to check
     */
    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    }

    /**
     * Determine whether the given method is an "equals" method.
     * 
     * @see java.lang.Object#equals(Object)
     */
    public static boolean isEqualsMethod(Method method) {
        if (method == null || !method.getName().equals("equals")) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        return (paramTypes.length == 1 && paramTypes[0] == Object.class);
    }

    /**
     * Determine whether the given method is a "hashCode" method.
     * 
     * @see java.lang.Object#hashCode()
     */
    public static boolean isHashCodeMethod(Method method) {
        return (method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is a "toString" method.
     * 
     * @see java.lang.Object#toString()
     */
    public static boolean isToStringMethod(Method method) {
        return (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is originally declared by
     * {@link java.lang.Object}.
     */
    public static boolean isObjectMethod(Method method) {
        if (method == null) {
            return false;
        }
        try {
            Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called when
     * actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     * 
     * @param field
     *            the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Make the given method accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called when
     * actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     * 
     * @param method
     *            the method to make accessible
     * @see java.lang.reflect.Method#setAccessible
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * Make the given constructor accessible, explicitly setting it accessible
     * if necessary. The {@code setAccessible(true)} method is only called when
     * actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     * 
     * @param ctor
     *            the constructor to make accessible
     * @see java.lang.reflect.Constructor#setAccessible
     */
    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);

        }
    }

    /**
     * Callback optionally used to filter fields to be operated on by a field
     * callback.
     */
    public interface FieldFilter {

        /**
         * Determine whether the given field matches.
         * 
         * @param field
         *            the field to check
         */
        boolean matches(Field field);
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

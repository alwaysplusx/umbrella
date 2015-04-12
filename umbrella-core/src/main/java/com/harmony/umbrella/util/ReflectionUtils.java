package com.harmony.umbrella.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Simple utility class for working with the reflection API and handling reflection
 * exceptions.
 *
 * <p>Only intended for internal use.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Costin Leau
 * @author Sam Brannen
 * @author Chris Beams
 * @since 1.2.2
 */
public abstract class ReflectionUtils {

	public static <T> T instantiateClass(Class<T> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isInterface()) {
			throw new IllegalArgumentException("Specified class is an interface");
		}
		try {
			return instantiateClass(clazz.getDeclaredConstructor());
		} catch (NoSuchMethodException ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("");
	}

	/**
	 * Convenience method to instantiate a class using the given constructor. As this
	 * method doesn't try to load classes by name, it should avoid class-loading issues.
	 * <p>Note that this method tries to set the constructor accessible if given a
	 * non-accessible (that is, non-public) constructor.
	 * 
	 * @param ctor
	 *            the constructor to instantiate
	 * @param args
	 *            the constructor arguments to apply
	 * @return the new instance
	 * @throws BeanInstantiationException
	 *             if the bean cannot be instantiated
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
	 * Handle the given reflection exception. Should only be called if no checked
	 * exception is expected to be thrown by the target method. <p>Throws the underlying
	 * RuntimeException or Error in case of an InvocationTargetException with such a root
	 * cause. Throws an IllegalStateException with an appropriate message else.
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
	 * Handle the given invocation target exception. Should only be called if no checked
	 * exception is expected to be thrown by the target method. <p>Throws the underlying
	 * RuntimeException or Error in case of such a root cause. Throws an
	 * IllegalStateException else.
	 * 
	 * @param ex
	 *            the invocation target exception to handle
	 */
	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the <em>target
	 * exception</em> of an {@link InvocationTargetException}. Should only be called if no
	 * checked exception is expected to be thrown by the target method. <p>Rethrows the
	 * underlying exception cast to an {@link RuntimeException} or {@link Error} if
	 * appropriate; otherwise, throws an {@link IllegalStateException}.
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
	 * Rethrow the given {@link Throwable exception}, which is presumably the <em>target
	 * exception</em> of an {@link InvocationTargetException}. Should only be called if no
	 * checked exception is expected to be thrown by the target method. <p>Rethrows the
	 * underlying exception cast to an {@link Exception} or {@link Error} if appropriate;
	 * otherwise, throws an {@link IllegalStateException}.
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
	 * Determine whether the given method explicitly declares the given exception or one
	 * of its superclasses, which means that an exception of that type can be propagated
	 * as-is within a reflective invocation.
	 * 
	 * @param method
	 *            the declaring method
	 * @param exceptionType
	 *            the exception to throw
	 * @return {@code true} if the exception can be thrown as-is; {@code false} if it
	 *         needs to be wrapped
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
	 * Make the given field accessible, explicitly setting it accessible if necessary. The
	 * {@code setAccessible(true)} method is only called when actually necessary, to avoid
	 * unnecessary conflicts with a JVM SecurityManager (if active).
	 * 
	 * @param field
	 *            the field to make accessible
	 * @see java.lang.reflect.Field#setAccessible
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * Make the given method accessible, explicitly setting it accessible if necessary.
	 * The {@code setAccessible(true)} method is only called when actually necessary, to
	 * avoid unnecessary conflicts with a JVM SecurityManager (if active).
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
	 * Make the given constructor accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called when actually
	 * necessary, to avoid unnecessary conflicts with a JVM SecurityManager (if active).
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
}

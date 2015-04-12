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
package com.harmony.umbrella.util.reflect;

import java.lang.reflect.Field;

import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class FieldUtils {

	/**
	 * <p>查找Class中符合指定名称的字段(在所有字段以及其所有父类的字段中查询: {@linkplain Class#getDeclaredFields()})
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
	 *            the name of the field (may be {@code null} if type is specified)
	 * @param type
	 *            the type of the field (may be {@code null} if name is specified)
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
		String setMethodName = toSetMethodName(fieldName);
		try {
			MethodUtils.invokeMethod(setMethodName, target, value);
		} catch (Exception e) {
			Field field = findField(target.getClass(), fieldName);
			if (field == null) {
				throw new IllegalArgumentException("field not find");
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

	}

	/**
	 * 设置字段值<p> Set the field represented by the supplied {@link Field field object} on
	 * the specified {@link Object target object} to the specified {@code value} . In
	 * accordance with {@link Field#set(Object, Object)} semantics, the new value is
	 * automatically unwrapped if the underlying field has a primitive type. <p>Thrown
	 * exceptions are handled via a call to {@link #handleReflectionException(Exception)}.
	 * 
	 * @param field
	 *            the field to set
	 * @param target
	 *            the target object on which to set the field
	 * @param value
	 *            the value to set; may be {@code null}
	 */
	public static void setFieldValue(Field field, Object target, Object value) {
		String setMethodName = toSetMethodName(field.getName());
		try {
			MethodUtils.invokeMethod(setMethodName, target, value);
		} catch (Exception e) {
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
	}

	public static Object getFieldValue(String fieldName, Object target) {
		String getMethodName = toGetMethodName(fieldName);
		try {
			return MethodUtils.invokeMethod(getMethodName, target);
		} catch (Exception e) {
			Field field = findField(target.getClass(), fieldName);
			if (field == null) {
				throw new IllegalArgumentException("field not find");
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
	}

	/**
	 * 获取字段值<p> Get the field represented by the supplied {@link Field field object} on
	 * the specified {@link Object target object}. In accordance with
	 * {@link Field#get(Object)} semantics, the returned value is automatically wrapped if
	 * the underlying field has a primitive type. <p>Thrown exceptions are handled via a
	 * call to {@link #handleReflectionException(Exception)}.
	 * 
	 * @param field
	 *            the field to get
	 * @param target
	 *            the target object from which to get the field
	 * @return the field's current value
	 */
	public static Object getFieldValue(Field field, Object target) {
		String getMethodName = toGetMethodName(field.getName());
		try {
			return MethodUtils.invokeMethod(getMethodName, target);
		} catch (Exception e) {
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
	}

	/**
	 * 将字段名转为getter的方法名
	 * 
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
	 * 
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
	 * Invoke the given callback on all fields in the target class, going up the class
	 * hierarchy to get all declared fields.
	 * 
	 * @param clazz
	 *            the target class to analyze
	 * @param fc
	 *            the callback to invoke for each field
	 */
	public static void doWithFields(Class<?> clazz, FieldCallback fc) {
		doWithFields(clazz, fc, null);
	}

	/**
	 * Invoke the given callback on all fields in the target class, going up the class
	 * hierarchy to get all declared fields.
	 * 
	 * @param clazz
	 *            the target class to analyze
	 * @param fc
	 *            the callback to invoke for each field
	 * @param ff
	 *            the filter that determines the fields to apply the callback to
	 */
	public static void doWithFields(Class<?> clazz, FieldCallback fc, FieldFilter ff) {
		// Keep backing up the inheritance hierarchy.
		Class<?> targetClass = clazz;
		do {
			Field[] fields = targetClass.getDeclaredFields();
			for (Field field : fields) {
				if (ff != null && !ff.matches(field)) {
					continue;
				}
				try {
					fc.doWith(field);
				} catch (IllegalAccessException ex) {
					throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
				}
			}
			targetClass = targetClass.getSuperclass();
		} while (targetClass != null && targetClass != Object.class);
	}

	/**
	 * Callback interface invoked on each field in the hierarchy.
	 */
	public interface FieldCallback {

		/**
		 * Perform an operation using the given field.
		 * 
		 * @param field
		 *            the field to operate on
		 */
		void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
	}

	/**
	 * Callback optionally used to filter fields to be operated on by a field callback.
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
}

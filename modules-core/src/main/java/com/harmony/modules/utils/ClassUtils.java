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
package com.harmony.modules.utils;

import java.util.List;

public abstract class ClassUtils {

	/** The package separator character '.' */
	static final char PACKAGE_SEPARATOR = '.';

	/** The path separator character '/' */
	static final char PATH_SEPARATOR = '/';

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

	public static String resourcePathAsClassPackage(String resourcePackage) {
		return resourcePackage.replace(PATH_SEPARATOR, PACKAGE_SEPARATOR);
	}

	/**
	 * 比较两个类是否相同
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static boolean equals(Class<?> c1, Class<?> c2) {
		return c1 == c2;
	}

	/**
	 * 比较两个类的全显定名称是否相同
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static boolean equalsIgnoreClassLoader(Class<?> c1, Class<?> c2) {
		return c1.getCanonicalName().equals(c2.getCanonicalName());
	}

	/**
	 * subClass是否是superClass的子类
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
		return superClass.isAssignableFrom(subClass);
	}

	/**
	 * 忽略类加载器的区别，比较subClass是否是superClass的子类
	 * @param superClass
	 * @param subClass
	 * @return
	 */
	public static boolean isAssignableIgnoreClassLoader(Class<?> superClass, Class<?> subClass) {
		if (equals(superClass, subClass))
			return true;
		if (superClass == null && subClass == null)
			return true;
		if (subClass == null || superClass == null)
			return false;
		Class<?> copySuperClass = superClass;
		Class<?> copySubClass = subClass;
		try {
			if (superClass.getClassLoader() != subClass.getClassLoader()) {
				ClassLoader loader = getDefaultClassLoader();
				copySuperClass = loader.loadClass(superClass.getName());
				copySubClass = loader.loadClass(subClass.getName());
			}
		} catch (ClassNotFoundException e) {
		}
		return copySuperClass.isAssignableFrom(copySubClass);
	}

	/**
	 * 检查输入类型是否符合模版的参数
	 * @param pattern 参数的模版
	 * @param inputTypes 输入类型
	 * @return
	 * @see ClassUtils#isAssignableIgnoreClassLoader(Class, Class)
	 */
	public static boolean typeEquals(Class<?>[] pattern, Class<?>[] inputTypes) {
		if (pattern.length != inputTypes.length)
			return false;
		for (int i = 0, max = pattern.length; i < max; i++) {
			if (!isAssignableIgnoreClassLoader(inputTypes[i], pattern[i]))
				return false;
		}
		return true;
	}

	/**
	 * @see #typeEquals(Class[], Class[])
	 */
	public static boolean typeEquals(List<Class<?>> pattern, List<Class<?>> inputTypes) {
		return typeEquals(pattern.toArray(new Class[pattern.size()]), inputTypes.toArray(new Class[inputTypes.size()]));
	}
}

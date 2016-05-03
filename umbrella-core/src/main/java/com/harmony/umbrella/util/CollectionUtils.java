package com.harmony.umbrella.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public class CollectionUtils {
	
	/**
	 * Return {@code true} if the supplied Collection is {@code null} or empty.
	 * Otherwise, return {@code false}.
	 * 
	 * @param collection
	 *            the Collection to check
	 * @return whether the given Collection is empty
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	/**
	 * Return {@code true} if the supplied Map is {@code null} or empty.
	 * Otherwise, return {@code false}.
	 * 
	 * @param map
	 *            the Map to check
	 * @return whether the given Map is empty
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}

    /**
     * 将Collection转为数组输出
     * 
     * @param c
     *            集合
     * @param arrayType
     *            数组类型, String -> String[]
     */
	@SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> c, Class<T> arrayType) {
        T[] array = (T[]) Array.newInstance(arrayType, c.size());
        return c.toArray(array);
    }

}

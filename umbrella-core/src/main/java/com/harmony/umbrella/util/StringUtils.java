package com.harmony.umbrella.util;

import java.lang.reflect.Method;

import org.springframework.util.Assert;

public class StringUtils extends org.springframework.util.StringUtils {

    /**
     * <p>
     * Checks if a String is whitespace, empty ("") or null.
     * </p>
     *
     * <pre>
     *  
     * StringUtils.isBlank(null) = true 
     * StringUtils.isBlank("") = true
     * StringUtils.isBlank(" ") = true 
     * StringUtils.isBlank("bob") = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str
     */
    public static boolean isBlank(String str) {
        return !hasText(str);
    }

    /**
     * <p>
     * Checks if a String is not empty (""), not null and not whitespace only.
     * </p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str
     *            the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null and not whitespace
     */
    public static boolean isNotBlank(String str) {
        return hasText(str);
    }

    /**
     * <p>
     * Checks if a String is empty ("") or null.
     * </p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str
     *            the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     */
    public static boolean isEmpty(String str) {
        return !hasLength(str);
    }

    /**
     * <p>
     * Checks if a String is not empty ("") and not null.
     * </p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str
     *            the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return hasLength(str);
    }

    /**
     * 生成一个方法id
     * 
     * @param method
     *            方法名
     * @return 方法id
     */
    public static String getMethodId(Method method) {
        Assert.notNull(method, "method must not be null");
        StringBuilder o = new StringBuilder();
        o.append(method.getDeclaringClass().getName())//
                .append("#").append(method.getName()).append("(");

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0, max = parameterTypes.length; i < max; i++) {
                o.append(parameterTypes[i].getName());
                if (i < max - 1) {
                    o.append(", ");
                }
            }
        }
        return o.append(")").toString();
    }

}

package com.harmony.umbrella.util;

import java.lang.reflect.Field;
import java.util.StringTokenizer;

import com.harmony.umbrella.core.accessor.ClassFieldAccessor;

/**
 * field / getter or setter method
 * 
 * @author wuxii@foxmail.com
 */
public class AccessUtils {

    public static Class<?> getType(String path, Class<?> targetClass) {
        ClassFieldAccessor cfa = new ClassFieldAccessor();
        Class<?> clazz = targetClass;
        StringTokenizer st = new StringTokenizer(path, ".");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            Field f = (Field) cfa.get(token, clazz);
            if (f != null) {
                clazz = f.getType();
            } else {
                throw new IllegalArgumentException(token + " field not exists in " + clazz.getName());
            }
        }
        return clazz;
    }

    public static void set(String path, Object target, Object value) {
    }

}
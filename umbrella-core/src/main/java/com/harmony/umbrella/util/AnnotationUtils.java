package com.harmony.umbrella.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public class AnnotationUtils {

    public static Object getAnnotationValue(Annotation ann, String methodName) {
        return getAnnotationValue(ann, methodName, Object.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationValue(Annotation ann, String methodName, Class<T> resultType) {
        try {
            Method method = ann.annotationType().getMethod(methodName);
            return (T) ReflectionUtils.invokeMethod(method, ann);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}

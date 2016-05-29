package com.harmony.umbrella.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, Object> toMap(Annotation ann) {
        Map<String, Object> result = new HashMap<String, Object>();
        Class<? extends Annotation> annotationType = ann.annotationType();
        Method[] methods = annotationType.getMethods();
        for (Method method : methods) {
            if (!ReflectionUtils.isObjectMethod(method)) {
                result.put(method.getName(), getAnnotationValue(ann, method.getName()));
            }
        }
        return result;
    }

}

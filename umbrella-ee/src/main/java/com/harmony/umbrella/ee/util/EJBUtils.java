package com.harmony.umbrella.ee.util;

import com.harmony.umbrella.util.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.ejb.*;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author wuxii@foxmail.com
 */
public class EJBUtils {

    private static final Class[] SESSION_ANNOTATIONS;

    static {
        List<Class<? extends Annotation>> annCls = new ArrayList<Class<? extends Annotation>>();
        annCls.add(Stateless.class);
        annCls.add(Stateful.class);
        annCls.add(Singleton.class);
        SESSION_ANNOTATIONS = annCls.toArray(new Class[0]);
    }

    public static boolean isStateless(Class<?> clazz) {
        return clazz.getAnnotation(Stateless.class) != null;
    }

    public static boolean isStateful(Class<?> clazz) {
        return clazz.getAnnotation(Stateful.class) != null;
    }

    public static boolean isSingleton(Class<?> clazz) {
        return clazz.getAnnotation(Singleton.class) != null;
    }

    public static boolean isSessionBean(Class<?> clazz) {
        for (Class annCls : SESSION_ANNOTATIONS) {
            if (clazz.getAnnotation(annCls) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRemoteClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Remote.class) != null;
    }

    public static boolean isLocalClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Local.class) != null;
    }

    public static String getName(Class<?> beanClass, Class<? extends Annotation>... sequence) {
        Object value = getProperty(beanClass, "name", sequence);
        return (value instanceof String && StringUtils.isNotBlank((String) value)) ? (String) value : null;
    }

    public static String getMappedName(Class<?> beanClass, Class<? extends Annotation>... sequence) {
        Object value = getProperty(beanClass, "mappedName", sequence);
        return (value instanceof String && StringUtils.isNotBlank((String) value)) ? (String) value : null;
    }

    private static Object getProperty(Class<?> clazz, String name, Class<? extends Annotation>... sequence) {
        if (sequence == null || sequence.length == 0) {
            sequence = SESSION_ANNOTATIONS;
        }
        Object value = null;
        for (Class<? extends Annotation> req : sequence) {
            Annotation resp = clazz.getAnnotation(req);
            value = AnnotationUtils.getValue(resp, name);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    public static Set<String> asSet(String text, String delimiter) {
        Set<String> result = new HashSet<>();
        if (text != null) {
            Collections.addAll(result, StringUtils.tokenizeToStringArray(text, delimiter));
        }
        return result;
    }
}

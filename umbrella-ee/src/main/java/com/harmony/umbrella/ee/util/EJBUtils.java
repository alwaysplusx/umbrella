package com.harmony.umbrella.ee.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("unchecked")
public class EJBUtils {

    private static final Class<? extends Annotation>[] SESSION_ANNOTATIONS;

    static {
        List<Class<? extends Annotation>> annCls = new ArrayList<Class<? extends Annotation>>();
        annCls.add(Stateless.class);
        annCls.add(Stateful.class);
        annCls.add(Singleton.class);
        SESSION_ANNOTATIONS = annCls.toArray(new Class[annCls.size()]);
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
        for (Class<? extends Annotation> annCls : SESSION_ANNOTATIONS) {
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

    public static String getName(Class<?> beanClass, Class<? extends Annotation>... seqance) {
        Object value = getProperty(beanClass, "name", seqance);
        return (value != null && value instanceof String && StringUtils.isNotBlank((String) value)) ? (String) value : null;
    }

    public static String getMappedName(Class<?> beanClass, Class<? extends Annotation>... seqance) {
        Object value = getProperty(beanClass, "mappedName", seqance);
        return (value != null && value instanceof String && StringUtils.isNotBlank((String) value)) ? (String) value : null;
    }

    public static Object getProperty(Class<?> clazz, String name, Class<? extends Annotation>... seqance) {
        if (seqance == null || seqance.length == 0) {
            seqance = SESSION_ANNOTATIONS;
        }
        Object value = null;
        for (Class<? extends Annotation> req : seqance) {
            Annotation resp = clazz.getAnnotation(req);
            value = AnnotationUtils.getAnnotationValue(resp, name);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    public static Set<String> asSet(String text, String delimiter) {
        Set<String> result = new HashSet<String>();
        if (text != null) {
            Collections.addAll(result, StringUtils.tokenizeToStringArray(text, delimiter));
        }
        return result;
    }
}

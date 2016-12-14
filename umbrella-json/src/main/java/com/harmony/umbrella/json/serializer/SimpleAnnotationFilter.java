package com.harmony.umbrella.json.serializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.core.Member;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleAnnotationFilter extends MemberFilterFilter {

    private final Set<Class<? extends Annotation>> annotationClasses = new HashSet<Class<? extends Annotation>>();

    private FilterMode filterMode;

    public SimpleAnnotationFilter() {
    }

    @SuppressWarnings("unchecked")
    public SimpleAnnotationFilter(Class<? extends Annotation>... annCls) {
        this.addAnnotationClass(annCls);
    }

    public SimpleAnnotationFilter(Class<? extends Annotation>[] annCls, FilterMode mode) {
        this.addAnnotationClass(annCls);
        this.filterMode = mode;
    }

    public SimpleAnnotationFilter(Collection<Class<? extends Annotation>> annCls, FilterMode mode) {
        this.addAnnotationClasses(annCls);
        this.filterMode = mode;
    }

    @Override
    protected boolean accept(Member member, Object target) {
        Method readMethod = member.getReadMethod();
        if (readMethod != null) {
            for (Class<? extends Annotation> annCls : annotationClasses) {
                if (readMethod.getAnnotation(annCls) != null) {
                    return !FilterMode.INCLUDE.equals(filterMode);
                }
            }
        }
        Field field = member.getField();
        if (field != null) {
            for (Class<? extends Annotation> annCls : annotationClasses) {
                if (field.getAnnotation(annCls) != null) {
                    return !FilterMode.INCLUDE.equals(filterMode);
                }
            }
        }
        return !FilterMode.INCLUDE.equals(filterMode);
    }

    @SuppressWarnings("unchecked")
    public void addAnnotationClass(Class<? extends Annotation>... annCls) {
        Collections.addAll(annotationClasses, annCls);
    }

    private void addAnnotationClasses(Collection<Class<? extends Annotation>> annCls) {
        this.annotationClasses.addAll(annCls);
    }

    public Set<Class<? extends Annotation>> getAnnotationClasses() {
        return annotationClasses;
    }

    public void setAnnotationClasses(Set<Class<? extends Annotation>> annotationClasses) {
        this.annotationClasses.clear();
        this.annotationClasses.addAll(annotationClasses);
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

}

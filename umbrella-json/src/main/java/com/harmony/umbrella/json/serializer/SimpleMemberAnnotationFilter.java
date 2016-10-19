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
public class SimpleMemberAnnotationFilter extends MemberFilterFilter {

    private final Set<Class<? extends Annotation>> annotationClasses = new HashSet<Class<? extends Annotation>>();

    private FilterMode filterMode;

    public SimpleMemberAnnotationFilter() {
    }

    @SuppressWarnings("unchecked")
    public SimpleMemberAnnotationFilter(Class<? extends Annotation>... annCls) {
        this.addAnnotationClass(annCls);
    }

    public SimpleMemberAnnotationFilter(Class<? extends Annotation>[] annCls, FilterMode mode) {
        this.addAnnotationClass(annCls);
        this.filterMode = mode;
    }

    public SimpleMemberAnnotationFilter(Collection<Class<? extends Annotation>> annCls, FilterMode mode) {
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

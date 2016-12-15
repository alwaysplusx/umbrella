package com.harmony.umbrella.json.serializer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.core.Member;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleAnnotationFilter extends MemberPropertyFilter {

    private final Set<Class<? extends Annotation>> annotationClasses = new HashSet<Class<? extends Annotation>>();

    private final boolean include;

    public SimpleAnnotationFilter() {
        this(new Class[0], FilterMode.EXCLUDE);
    }

    public SimpleAnnotationFilter(FilterMode mode) {
        this(new Class[0], mode);
    }

    public SimpleAnnotationFilter(Class<? extends Annotation>... annCls) {
        this(annCls, FilterMode.EXCLUDE);
    }

    public SimpleAnnotationFilter(Class<? extends Annotation>[] annCls, FilterMode mode) {
        this(Arrays.asList(annCls), mode);
    }

    public SimpleAnnotationFilter(Collection<Class<? extends Annotation>> annCls, FilterMode mode) {
        super(true);
        this.include = (FilterMode.INCLUDE == mode);
        this.annotationClasses.addAll(annCls);
    }

    @Override
    protected boolean accept(Member member, Object target) {
        for (Class<? extends Annotation> annCls : annotationClasses) {
            if (member.getAnnotation(annCls) != null) {
                return include;
            }
        }
        return !include;
    }

    public Set<Class<? extends Annotation>> getAnnotationClasses() {
        return annotationClasses;
    }

    public void setAnnotationClasses(Collection<Class<? extends Annotation>> annotationClasses) {
        this.annotationClasses.clear();
        this.annotationClasses.addAll(annotationClasses);
    }

    public FilterMode getFilterMode() {
        return include ? FilterMode.INCLUDE : FilterMode.EXCLUDE;
    }

}

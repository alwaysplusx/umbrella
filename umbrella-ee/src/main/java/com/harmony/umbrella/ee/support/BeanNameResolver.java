package com.harmony.umbrella.ee.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class BeanNameResolver implements PartResolver<String> {

    static final List<Class<? extends Annotation>> SESSION_ANNOTATION = new ArrayList<Class<? extends Annotation>>();

    static {
        SESSION_ANNOTATION.add(Stateless.class);
        SESSION_ANNOTATION.add(Stateful.class);
        SESSION_ANNOTATION.add(Singleton.class);
    }

    @Override
    public Set<String> resolve(BeanDefinition bd) {
        Set<String> result = new HashSet<String>();
        for (Class<? extends Annotation> annCls : SESSION_ANNOTATION) {
            Annotation ann = bd.getAnnotation(annCls);
            if (ann != null) {
                String name = (String) AnnotationUtils.getAnnotationValue(ann, "name");
                if (StringUtils.isBlank(name)) {
                    name = (String) AnnotationUtils.getAnnotationValue(ann, "mappedName");
                }
                if (StringUtils.isNotBlank(name)) {
                    result.add(name);
                }
            }
        }
        if (!result.isEmpty()) {
            return result;
        }
        Class<?> beanClass = bd.getBeanClass();
        if (!beanClass.isInterface()) {
            result.add(beanClass.getSimpleName());
        }
        return result;
    }

}

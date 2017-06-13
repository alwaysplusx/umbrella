package com.harmony.umbrella.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    private Class<? extends Annotation>[] autowrieAnnotations;

    public AbstractBeanFactory() {
    }

    public AbstractBeanFactory(Class<? extends Annotation>... anns) {
        this.autowrieAnnotations = anns;
    }

    protected abstract Object getBean(Field field);

    protected abstract Object getBean(Method method);

    @Override
    public void autowrie(Object existingBean) throws BeansException {
        if (existingBean == null) {
            return;
        }
        Class<?> beanClass = existingBean.getClass();
        List<Field> autowireFields = getAutowireFields(beanClass);
        for (Field field : autowireFields) {
            Object bean = getBean(field);
            ReflectionUtils.setField(field, existingBean, bean);
        }

        List<Method> autowireMethods = getAutowireMethods(beanClass);
        for (Method method : autowireMethods) {
            Object bean = getBean(method);
            ReflectionUtils.invokeMethod(method, existingBean, bean);
        }
    }

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        try {
            return (T) getBean(beanName, Class.forName(beanName));
        } catch (ClassNotFoundException e) {
            return (T) getBean(beanName, Object.class);
        }
    }

    public Class<? extends Annotation>[] getAutowrieAnnotations() {
        return autowrieAnnotations;
    }

    public void setAutowrieAnnotations(Class<? extends Annotation>[] autowrieAnnotations) {
        this.autowrieAnnotations = autowrieAnnotations;
    }

    protected List<Method> getAutowireMethods(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return Collections.emptyList();
        }
        List<Method> result = new ArrayList<Method>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (isAutowireMethod(method)) {
                result.add(method);
            }
        }
        result.addAll(getAutowireMethods(clazz.getSuperclass()));
        return result;
    }

    protected List<Field> getAutowireFields(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return Collections.emptyList();
        }
        List<Field> result = new ArrayList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                if (isAutowireField(field)) {
                    result.add(field);
                }
            }
        }
        result.addAll(getAutowireFields(clazz.getSuperclass()));
        return result;
    }

    protected boolean isAutowireMethod(Method method) {
        if (method.getParameterTypes().length != 1 //
                || method.getName().length() < 4//
                || !method.getName().startsWith("set") //
                || ReflectionUtils.isObjectMethod(method) //
                || Modifier.isStatic(method.getModifiers()) //
                || !Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        for (Class<? extends Annotation> annCls : autowrieAnnotations) {
            if (method.getAnnotation(annCls) != null) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAutowireField(Field field) {
        if (autowrieAnnotations != null) {
            for (Class<? extends Annotation> annCls : autowrieAnnotations) {
                if (!Modifier.isStatic(field.getModifiers()) && field.getAnnotation(annCls) != null) {
                    return true;
                }
            }
        }
        return false;
    }

}

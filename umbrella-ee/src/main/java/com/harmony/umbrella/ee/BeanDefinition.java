package com.harmony.umbrella.ee;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * JavaEE {@linkplain Stateless}, {@linkplain Stateful}, {@linkplain Singleton}
 * 为sessionBean
 * <p/>
 * 将标记了这些注解的bean的基础信息定义为{@linkplain BeanDefinition}
 *
 * @author wuxii@foxmail.com
 */
public class BeanDefinition {

    static final List<Class<? extends Annotation>> SESSION_ANNOTATION = new ArrayList<Class<? extends Annotation>>();

    static {
        SESSION_ANNOTATION.add(Stateless.class);
        SESSION_ANNOTATION.add(Stateful.class);
        SESSION_ANNOTATION.add(Singleton.class);
    }

    /**
     * beanClass 会话bean的类, 如果class没有标注session bean/local的注解并且是接口, 默认认为是remote接口
     */
    public final Class<?> beanClass;

    private final Class<?>[] remoteClasses;

    public final Annotation sessionAnnotation;

    public BeanDefinition(Class<?> beanClass) {
        this(beanClass, getSessionBeanAnnotation(beanClass));
    }

    private BeanDefinition(Class<?> beanClass, Annotation ann) {
        this.beanClass = beanClass;
        this.sessionAnnotation = ann;
        this.remoteClasses = findRemoteClass(beanClass);
    }

    /**
     * bean 类型
     *
     * @return 被描述的类
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Class<?>[] getRemoteClasses() {
        return remoteClasses;
    }

    @SuppressWarnings("rawtypes")
    private Class[] findRemoteClass(Class<?> clazz) {
        List<Class> remoteClasses = new ArrayList<Class>();
        if (isRemoteClass(clazz) || (clazz.isInterface() && !isLocalClass(clazz))) {
            remoteClasses.add(clazz);
        } else {
            Remote ann = clazz.getAnnotation(Remote.class);
            if (ann != null) {
                Class[] classes = ann.value();
                for (Class c : classes) {
                    if (isRemoteClass(c)) {
                        remoteClasses.add(c);
                    }
                }
            }
            Class<?>[] interfaces = ClassUtils.getAllInterfaces(beanClass);
            for (Class<?> c : interfaces) {
                if (isRemoteClass(c)) {
                    remoteClasses.add(c);
                }
            }
        }
        return remoteClasses.toArray(new Class[remoteClasses.size()]);
    }

    /**
     * beanClass 标记了{@linkplain Stateless}
     */
    public boolean isStateless() {
        return isThatOf(Stateless.class);
    }

    /**
     * beanClass 标记了{@linkplain Stateful}
     */
    public boolean isStateful() {
        return isThatOf(Stateful.class);
    }

    /**
     * beanClass 标记了{@linkplain Singleton}
     */
    public boolean isSingleton() {
        return isThatOf(Singleton.class);
    }

    /**
     * 测试beanClass上注解的是否是annClass
     *
     * @param annClass
     * @return
     */
    public boolean isThatOf(Class<? extends Annotation> annClass) {
        return sessionAnnotation != null && sessionAnnotation.getClass() == annClass;
    }

    // 注解信息
    public String getAnnotationValue(String name) {
        return (String) (sessionAnnotation == null ? null : AnnotationUtils.getAnnotationValue(sessionAnnotation, name));
    }

    /**
     * 获取beanClass上的SessionBean的注解, 判断注解上的mappedName, 为空则默认为类名的SampleName
     * <p/>
     * 如果为标注SessionBean注解返回null
     */
    public String getMappedName() {
        String mappedName = getAnnotationValue("mappedName");
        if (StringUtils.isBlank(mappedName) && isSessionClass()) {
            return beanClass.getSimpleName();
        }
        return mappedName;
    }

    /**
     * @see Stateless#name()
     */
    public String getName() {
        return getAnnotationValue("name");
    }

    /**
     * @see Stateless#description()
     */
    public String getDescription() {
        return getAnnotationValue("description");
    }

    /**
     * 注有{@linkplain Stateless}, {@linkplain Stateful}, {@linkplain Singleton}
     * 三类注解中的一个则为sessionBean
     */
    public boolean isSessionClass() {
        return sessionAnnotation != null;
    }

    /**
     * beanClass是interface并且接口标记了{@linkplain Remote}注解
     * <p/>
     * <b>默认将不是local的interface定义为remote接口</b>
     */
    public boolean isRemoteClass() {
        return isRemoteClass(beanClass) || (beanClass.isInterface() && !isLocalClass(beanClass));
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BeanDefinition that = (BeanDefinition) o;

        if (sessionAnnotation != null ? !sessionAnnotation.equals(that.sessionAnnotation) : that.sessionAnnotation != null)
            return false;
        if (beanClass != null ? !beanClass.equals(that.beanClass) : that.beanClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = beanClass != null ? beanClass.hashCode() : 0;
        result = 31 * result + (sessionAnnotation != null ? sessionAnnotation.hashCode() : 0);
        return result;
    }

    // utils method
    private boolean isRemoteClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Remote.class) != null;
    }

    private boolean isLocalClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Local.class) != null;
    }

    private static Annotation getSessionBeanAnnotation(Class<?> clazz) {
        for (Class<? extends Annotation> sc : SESSION_ANNOTATION) {
            Annotation ann = clazz.getAnnotation(sc);
            if (ann != null) {
                return ann;
            }
        }
        return null;
    }
}

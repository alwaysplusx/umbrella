package com.harmony.umbrella.ee;

import java.lang.annotation.Annotation;

import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.BeansException;
import com.harmony.umbrella.beans.NoSuchBeanFoundException;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * JavaEE的应用上下文实现
 *
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContext extends ApplicationContext implements EJBBeanFactory {

    private static final Log log = Logs.getLog(EJBApplicationContext.class);

    private EJBBeanFactory beanFactory;

    private org.springframework.context.ApplicationContext springContext;

    public EJBApplicationContext(org.springframework.context.ApplicationContext springContext) {
        this.springContext = springContext;
    }

    public EJBApplicationContext() {
        try {
            beanFactory = (EJBBeanFactory) springContext.getBean("beanFactory");
            log.debug("find bean factory by bean name beanFactory");
        } catch (org.springframework.beans.BeansException e) {
            beanFactory = springContext.getBean(EJBBeanFactory.class);
            log.debug("find bean factory by type");
            throw new NoSuchBeanFoundException("no bean named 'beanFactory' or type is " + EJBBeanFactory.class.getName(), e);
        } catch (ClassCastException e) {
            log.error("bean factory mismatch", e);
            throw e;
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void autowrie(Object bean) throws BeansException {
        beanFactory.autowrie(bean);
    }

    @Override
    public <T> T lookup(String jndi) throws BeansException {
        return beanFactory.lookup(jndi);
    }

    @Override
    public <T> T lookup(Class<T> clazz) throws BeansException {
        return beanFactory.lookup(clazz);
    }

    @Override
    public <T> T lookup(Class<T> clazz, Annotation... ann) throws BeansException {
        return beanFactory.lookup(clazz, ann);
    }

    @Override
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public void destroy() {
    }

}

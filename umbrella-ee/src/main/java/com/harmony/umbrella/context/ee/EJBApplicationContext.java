package com.harmony.umbrella.context.ee;

import javax.ejb.EJB;

import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.BeansException;
import com.harmony.umbrella.context.ApplicationContext;

/**
 * JavaEE的应用上下文实现
 *
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContext extends ApplicationContext implements EJBBeanFactory {

    // private static final Log log = Logs.getLog(EJBApplicationContext.class);

    private EJBBeanFactory beanFactory;

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
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
    public <T> T lookup(Class<T> clazz, EJB ejbAnnotation) throws BeansException {
        return beanFactory.lookup(clazz, ejbAnnotation);
    }

    @Override
    public <T> T lookup(BeanDefinition beanDefinition) throws BeansException {
        return beanFactory.lookup(beanDefinition);
    }

    @Override
    public <T> T lookup(BeanDefinition beanDefinition, EJB ejbAnnotation) throws BeansException {
        return beanFactory.lookup(beanDefinition, ejbAnnotation);
    }

    @Override
    public BeanFactory getBeanFactory() {
        return null;
    }

}

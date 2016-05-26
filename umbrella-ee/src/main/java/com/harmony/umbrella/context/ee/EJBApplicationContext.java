package com.harmony.umbrella.context.ee;

import javax.ejb.EJB;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.core.BeansException;

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
    public <T> T getBean(String beanName) throws BeansException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, String scope) throws BeansException {
        return beanFactory.getBean(beanName, scope);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return beanFactory.getBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws BeansException {
        return beanFactory.getBean(beanClass, scope);
    }

    @Override
    public <T> Object lookup(String jndi) throws BeansException {
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

}

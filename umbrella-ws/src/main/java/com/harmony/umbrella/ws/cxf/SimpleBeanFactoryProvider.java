package com.harmony.umbrella.ws.cxf;

import org.apache.cxf.message.Message;

import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.NoSuchBeanFoundException;
import com.harmony.umbrella.beans.SimpleBeanFactory;
import com.harmony.umbrella.ws.jaxrs.JaxRsServerBuilder.BeanFactoryProvider;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleBeanFactoryProvider implements BeanFactoryProvider {

    private final Class<?> resourceClass;
    private boolean singleton;

    private BeanFactory beanFactory;

    public SimpleBeanFactoryProvider(Class<?> resourceClass) {
        this(resourceClass, new SimpleBeanFactory(), false);
    }

    public SimpleBeanFactoryProvider(Class<?> resourceClass, BeanFactory beanFactory) {
        this(resourceClass, beanFactory, false);
    }

    public SimpleBeanFactoryProvider(Class<?> resourceClass, BeanFactory beanFactory, boolean singleton) {
        this.resourceClass = resourceClass;
        this.beanFactory = beanFactory;
        this.singleton = singleton;
    }

    @Override
    public Object getInstance(Message m) {
        return getBean(resourceClass, isSingleton() ? SINGLETON : PROTOTYPE);
    }

    @Override
    public void releaseInstance(Message m, Object o) {
        // nothing
    }

    @Override
    public Class<?> getResourceClass() {
        return resourceClass;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public <T> T getBean(String beanName) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, String scope) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanName, scope);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanClass, scope);
    }

}

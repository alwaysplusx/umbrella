package com.harmony.umbrella.ws.cxf;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.invoker.AbstractInvoker;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.ws.jaxws.JaxWsServerBuilder.BeanFactoryInvoker;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleBeanFactoryInvoker extends AbstractInvoker implements BeanFactoryInvoker {

    private static final Log log = Logs.getLog(SimpleBeanFactoryInvoker.class);
    protected final Class<?> serviceClass;
    private BeanFactory beanFactory;

    public SimpleBeanFactoryInvoker(Class<?> serviceClass) {
        this(serviceClass, SimpleBeanFactory.INSTANCE);
    }

    public SimpleBeanFactoryInvoker(Class<?> serviceClass, BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.serviceClass = serviceClass;
    }

    @Override
    public <T> T getBean(String beanName) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanClass);
    }

    @Override
    public Object getServiceObject(Exchange context) {
        log.debug("get instance [{}] from [{}]", serviceClass.getName(), beanFactory);
        return getBean(serviceClass);
    }

    @Override
    public void autowrie(Object bean) throws BeansException {
        beanFactory.autowrie(bean);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requireType) throws BeansException {
        return beanFactory.getBean(beanName, requireType);
    }

}

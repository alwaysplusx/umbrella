package com.harmony.umbrella.ws.jaxrs;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.message.Message;

import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.NoSuchBeanFoundException;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.ws.FactoryConfig;
import com.harmony.umbrella.ws.cxf.SimpleBeanFactoryProvider;

/**
 * @author wuxii@foxmail.com
 */
public class JaxRsServerBuilder {

    private static final Log log = Logs.getLog(JaxRsServerBuilder.class);

    private final JAXRSServerFactoryBean serverFactoryBean;

    private Class<?> resourceClass;

    private Object resourceBean;

    /**
     * 发布的地址
     */
    private String address;

    /**
     * 是否单个服务实例, default is true
     */
    private boolean singleInstance = true;

    private BeanFactoryProvider beanFactoryProvider;

    private JaxRsServerBuilder() {
        this.serverFactoryBean = new JAXRSServerFactoryBean();
    }

    public static JaxRsServerBuilder create() {
        return new JaxRsServerBuilder();
    }

    private List<Interceptor<? extends Message>> getInInterceptors() {
        return serverFactoryBean.getInInterceptors();
    }

    private List<Interceptor<? extends Message>> getOutInterceptors() {
        return serverFactoryBean.getOutInterceptors();
    }

    private List<Interceptor<? extends Message>> getInFaultInterceptors() {
        return serverFactoryBean.getInFaultInterceptors();
    }

    private List<Interceptor<? extends Message>> getOutFaultInterceptors() {
        return serverFactoryBean.getOutFaultInterceptors();
    }

    public JaxRsServerBuilder addInInterceptor(Interceptor<? extends Message> interceptor) {
        getInInterceptors().add(interceptor);
        return this;
    }

    public JaxRsServerBuilder addOutInterceptor(Interceptor<? extends Message> interceptor) {
        getOutInterceptors().add(interceptor);
        return this;
    }

    public JaxRsServerBuilder addInFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getInFaultInterceptors().add(interceptor);
        return this;
    }

    public JaxRsServerBuilder addOutFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getOutFaultInterceptors().add(interceptor);
        return this;
    }

    /**
     * 设置服务地址
     * 
     * @param address
     *            服务地址
     * @return current builder
     */
    public JaxRsServerBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public JaxRsServerBuilder setResourceClass(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
        return this;
    }

    public JaxRsServerBuilder setBeanFactoryProvider(BeanFactoryProvider provider) {
        this.beanFactoryProvider = provider;
        return this;
    }

    public JaxRsServerBuilder setResourceBean(Object resourceBean) {
        this.resourceBean = resourceBean;
        return this;
    }

    public JaxRsServerBuilder setSingleInstance(boolean singleInstance) {
        this.singleInstance = singleInstance;
        return this;
    }

    // above is server basic properties

    public Server publish() {
        return doPublish(null);
    }

    public Server publish(Object resourceBean) {
        this.resourceBean = resourceBean;
        return doPublish(null);
    }

    public Server publish(Object resourceBean, String address) {
        this.resourceBean = resourceBean;
        this.address = address;
        return doPublish(null);
    }

    public Server publish(Object resourceBean, FactoryConfig<JAXRSServerFactoryBean> factoryConfig) {
        this.resourceBean = resourceBean;
        return doPublish(factoryConfig);
    }

    public Server publish(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
        return doPublish(null);
    }

    public Server publish(Class<?> resourceClass, String address) {
        this.resourceClass = resourceClass;
        this.address = address;
        return doPublish(null);
    }

    public Server publish(Class<?> resourceClass, FactoryConfig<JAXRSServerFactoryBean> factoryConfig) {
        this.resourceClass = resourceClass;
        return doPublish(factoryConfig);
    }

    private Server doPublish(FactoryConfig<JAXRSServerFactoryBean> factoryConfig) {
        Assert.isTrue(resourceClass != null || resourceBean != null, "please set at least one service properties bean or class");
        Assert.notBlank(address, "server address is null or blank");

        if (factoryConfig != null) {
            factoryConfig.config(serverFactoryBean);
            if (serverFactoryBean.getServer() != null) {
                throw new IllegalStateException("config factory not allow call create method");
            }
        }

        if (resourceBean != null || singleInstance) {
            if (resourceBean == null) {
                try {
                    resourceBean = getBeanFactoryProvider().getBean(resourceClass);
                } catch (NoSuchBeanFoundException e) {
                    throw new WebServiceException("can't create service bean", e);
                }
            }
            serverFactoryBean.setServiceBean(resourceBean);
        } else {
            serverFactoryBean.setResourceClasses(resourceClass);
            serverFactoryBean.setResourceProvider(getBeanFactoryProvider());
        }

        serverFactoryBean.setAddress(address);
        Server server = serverFactoryBean.create();

        log.debug("create jaxrs server [{}@{}] success", resourceBean == null ? resourceClass.getName() : resourceBean, address);

        return server;
    }

    /**
     * 获取builder中的信息
     * 
     * @param cls
     *            待获取的类
     * @param <T>
     *            返回的类型
     * @return 指定的类信息
     * @throws IllegalArgumentException
     *             不支持的类型
     */
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> cls) {
        if (JAXRSServerFactoryBean.class.isAssignableFrom(cls)) {
            return (T) serverFactoryBean;
        }
        throw new IllegalArgumentException("Unsupported unwrap target type [" + cls.getName() + "]");
    }

    private BeanFactoryProvider getBeanFactoryProvider() {
        if (beanFactoryProvider == null) {
            beanFactoryProvider = new SimpleBeanFactoryProvider(resourceClass);
        }
        return beanFactoryProvider;
    }

    public interface BeanFactoryProvider extends ResourceProvider, BeanFactory {

    }

}

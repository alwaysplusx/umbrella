/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.ws.jaxws;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.invoker.Invoker;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.ws.FactoryConfig;
import com.harmony.umbrella.ws.cxf.SimpleBeanFactoryInvoker;

/**
 * 服务建造者
 * <p>
 * 为方便使用,方法采用链式结构
 * <p>
 * <em>one server one builder</em>
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsServerBuilder {

    private static final Log log = Logs.getLog(JaxWsServerBuilder.class);

    /**
     * 创建服务的服务工厂
     */
    private final JaxWsServerFactoryBean serverFactoryBean = new JaxWsServerFactoryBean();

    /**
     * 服务的实现
     */
    private Class<?> serviceClass;

    /**
     * 服务实例
     */
    private Object serviceBean;

    /**
     * 是否单个服务实例, default is true
     */
    private boolean singleInstance = true;

    /**
     * 发布的地址
     */
    private String address;

    /**
     * Deprecated use {@link #getBeanFactoryInvoker()} method
     */
    private BeanFactoryInvoker beanFactoryInvoker;

    private JaxWsServerBuilder() {
    }

    public static JaxWsServerBuilder create() {
        return new JaxWsServerBuilder();
    }

    public List<Interceptor<? extends Message>> getInInterceptors() {
        return serverFactoryBean.getInInterceptors();
    }

    public List<Interceptor<? extends Message>> getOutInterceptors() {
        return serverFactoryBean.getOutInterceptors();
    }

    public List<Interceptor<? extends Message>> getInFaultInterceptors() {
        return serverFactoryBean.getInFaultInterceptors();
    }

    public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
        return serverFactoryBean.getOutFaultInterceptors();
    }

    public JaxWsServerBuilder addInInterceptor(Interceptor<? extends Message> interceptor) {
        getInInterceptors().add(interceptor);
        return this;
    }

    public JaxWsServerBuilder addOutInterceptor(Interceptor<? extends Message> interceptor) {
        getOutInterceptors().add(interceptor);
        return this;
    }

    public JaxWsServerBuilder addInFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getInFaultInterceptors().add(interceptor);
        return this;
    }

    public JaxWsServerBuilder addOutFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getOutFaultInterceptors().add(interceptor);
        return this;
    }

    public JaxWsServerBuilder setBeanFactoryInvoker(BeanFactoryInvoker beanFactoryInvoker) {
        this.beanFactoryInvoker = beanFactoryInvoker;
        return this;
    }

    /**
     * 设置服务实现类
     * 
     * @param serviceClass
     *            服务实现类
     * @return current builder
     */
    public JaxWsServerBuilder setServiceClass(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
        return this;
    }

    /**
     * 设置服务地址
     * 
     * @param address
     *            服务地址
     * @return current builder
     */
    public JaxWsServerBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    /**
     * 设置服务bean
     * 
     * @param serviceBean
     *            服务bean
     */
    public JaxWsServerBuilder setServiceBean(Object serviceBean) {
        this.serviceBean = serviceBean;
        return this;
    }

    public JaxWsServerBuilder setSingleInstance(boolean singleInstance) {
        this.singleInstance = singleInstance;
        return this;
    }

    // above is server basic properties

    public Server publish() {
        return doPublish(null);
    }

    public Server publish(Object serverBean) {
        this.serviceBean = serverBean;
        return doPublish(null);
    }

    public Server publish(Object serverBean, String address) {
        this.serviceBean = serverBean;
        this.address = address;
        return doPublish(null);
    }

    public Server publish(Object serverBean, FactoryConfig<JaxWsServerFactoryBean> factoryConfig) {
        this.serviceBean = serverBean;
        return doPublish(factoryConfig);
    }

    public Server publish(Class<?> serviceClass) {
        return doPublish(null);
    }

    public Server publish(Class<?> serviceClass, String address) {
        this.serviceClass = serviceClass;
        this.address = address;
        return doPublish(null);
    }

    public Server publish(Class<?> serviceClass, String address, FactoryConfig<JaxWsServerFactoryBean> factoryConfig) {
        this.serviceClass = serviceClass;
        this.address = address;
        return doPublish(factoryConfig);
    }

    public Server publish(String address) {
        this.address = address;
        return doPublish(null);
    }

    public Server publish(String address, FactoryConfig<JaxWsServerFactoryBean> factoryConfig) {
        this.address = address;
        return doPublish(factoryConfig);
    }

    public Server publish(FactoryConfig<JaxWsServerFactoryBean> factoryConfig) {
        return doPublish(factoryConfig);
    }

    private Server doPublish(FactoryConfig<JaxWsServerFactoryBean> factoryConfig) {
        Assert.isTrue(serviceBean != null || serviceClass != null, "please set at least one service properties bean or class");
        Assert.notBlank(address, "server address is null or blank");

        if (factoryConfig != null) {
            factoryConfig.config(serverFactoryBean);
            if (serverFactoryBean.getServer() != null) {
                throw new WebServiceException("config factory not allow call create method");
            }
        }

        if (serviceBean != null || singleInstance) {
            if (serviceBean == null) {
                try {
                    serviceBean = getBeanFactoryInvoker().getBean(serviceClass);
                } catch (NoSuchBeanFoundException e) {
                    throw new WebServiceException("can't create service bean", e);
                }
            }
            serverFactoryBean.setServiceBean(serviceBean);
            serverFactoryBean.setServiceClass(serviceClass);
        } else {
            serverFactoryBean.setServiceClass(serviceClass);
            serverFactoryBean.setInvoker(getBeanFactoryInvoker());
        }

        serverFactoryBean.setAddress(address);
        Server server = serverFactoryBean.create();

        log.debug("create jaxws server [{}@{}] success", serviceBean == null ? serviceClass.getName() : serviceBean, address);

        return server;
    }

    /**
     * 获取builder中的信息
     * 
     * @param cls
     *            待获取的类
     * @return 指定的类信息
     * @throws IllegalArgumentException
     *             不支持的类型
     */
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> cls) {
        if (ServerFactoryBean.class.isAssignableFrom(cls)) {
            return (T) serverFactoryBean;
        }
        throw new IllegalArgumentException("Unsupported unwrap target type [" + cls.getName() + "]");
    }

    /**
     * 懒初始化
     * <p>
     * 当beanFactoryInvoker为空时候才使用默认的{@linkplain SimpleBeanFactoryInvoker}
     */
    private BeanFactoryInvoker getBeanFactoryInvoker() {
        if (beanFactoryInvoker == null) {
            beanFactoryInvoker = new SimpleBeanFactoryInvoker(serviceClass);
        }
        return beanFactoryInvoker;
    }

    public interface BeanFactoryInvoker extends Invoker, BeanFactory {

    }

}

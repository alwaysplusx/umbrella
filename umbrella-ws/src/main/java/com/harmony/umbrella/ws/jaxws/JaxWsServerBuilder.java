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

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;
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

    private static final Logger log = LoggerFactory.getLogger(JaxWsServerBuilder.class);

    /**
     * 创建服务的服务工厂
     */
    private final JaxWsServerFactoryBean serverFactoryBean;

    /**
     * 服务的接口,可为空
     */
    protected Class<?> serviceInterface;

    /**
     * 服务的实现
     */
    protected Class<?> serviceClass;

    /**
     * 发布的地址
     */
    protected String address;

    /**
     * 访问服务的用户名
     */
    protected String username;

    /**
     * 访问服务的密码
     */
    protected String password;

    /**
     * Deprecated use {@link #getBeanFactoryInvoker()} method
     */
    private BeanFactoryInvoker beanFactoryInvoker;

    protected JaxWsServerBuilder() {
        this.serverFactoryBean = new JaxWsServerFactoryBean();
    }

    public static JaxWsServerBuilder create() {
        return new JaxWsServerBuilder();
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

    public Server publish() {
        return publish(serviceClass, address, null);
    }

    public Server publish(Class<?> serviceClass) {
        return publish(serviceClass, address, null);
    }

    public Server publish(Class<?> serviceClass, String address) {
        return publish(serviceClass, address, null);
    }

    public Server publish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
        this.serviceClass = serviceClass;
        this.address = address;
        return doPublish(serviceClass, factoryConfig);
    }

    protected Server doPublish(Class<?> serviceClass, JaxWsServerFactoryConfig factoryConfig) {
        Assert.notNull(serviceClass, "service class is null");
        Assert.isTrue(StringUtils.isNotBlank(address), "server address is null or blank");

        if (factoryConfig != null) {
            factoryConfig.config(serverFactoryBean);
            if (serverFactoryBean.getServer() != null) {
                throw new IllegalStateException("config factory not allow call create method");
            }
        }

        applyPrefectServiceClass(serverFactoryBean, serviceClass, serviceInterface);

        serverFactoryBean.setInvoker(getBeanFactoryInvoker());
        serverFactoryBean.setAddress(address);
        Server server = serverFactoryBean.create();

        log.debug("create server success");
        return server;
    }

    /**
     * 给当前工厂设置最优的接口类
     */
    protected void applyPrefectServiceClass(JaxWsServerFactoryBean factoryBean, Class<?> serviceClass, Class<?> serviceInterface) {
        factoryBean.setServiceClass(serviceClass);
    }

    /**
     * 懒初始化
     * <p>
     * 当beanFactoryInvoker为空时候才使用默认的{@linkplain SimpleBeanFactoryInvoker}
     */
    protected BeanFactoryInvoker getBeanFactoryInvoker() {
        if (beanFactoryInvoker == null) {
            beanFactoryInvoker = new SimpleBeanFactoryInvoker(serviceClass);
        }
        return beanFactoryInvoker;
    }

    public JaxWsServerBuilder setBeanFactoryInvoker(BeanFactoryInvoker beanFactoryInvoker) {
        this.beanFactoryInvoker = beanFactoryInvoker;
        return this;
    }

    /**
     * 设置服务接口
     * 
     * @param serviceInterface
     *            服务接口
     * @return current builder
     */
    public JaxWsServerBuilder setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
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
     * 设置服务用户名
     * 
     * @param username
     *            用户名
     * @return current builder
     */
    public JaxWsServerBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 设置服务密码
     * 
     * @param password
     *            用户密码
     * @return current builder
     */
    public JaxWsServerBuilder setPassword(String password) {
        this.password = password;
        return this;
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
     * 服务创建前的回调配置
     * 
     * @author wuxii@foxmail.com
     */
    public interface JaxWsServerFactoryConfig {

        void config(JaxWsServerFactoryBean factoryBean);

    }

    public interface BeanFactoryInvoker extends Invoker, BeanFactory {

    }

}

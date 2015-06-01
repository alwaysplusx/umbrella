/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.jaxws.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.JaxWsExecutor;
import com.harmony.umbrella.jaxws.JaxWsMetadata;
import com.harmony.umbrella.jaxws.JaxWsMetadataLoader;
import com.harmony.umbrella.jaxws.JaxWsPhaseVisitor;
import com.harmony.umbrella.jaxws.impl.JaxWsCXFExecutor;
import com.harmony.umbrella.jaxws.impl.SimpleJaxWsContext;
import com.harmony.umbrella.util.PropUtils;

/**
 * 基于配置文件jaxws-receiver.txt的接收
 * 
 * @author wuxii@foxmail.com
 */
@Deprecated
public class PropertiesFileJaxWsContextReceiver implements JaxWsContextReceiver {

    protected final static String JAXWS_HANDLERS_LOCATION = "META-INF/jaxws/jaxws-receiver.txt";

    protected static final Logger log = LoggerFactory.getLogger(PropertiesFileJaxWsContextReceiver.class);

    /**
     * 用于加载用户名密码地址
     */
    private JaxWsMetadataLoader metaLoader;

    /**
     * JaxWsExecutor执行者
     */
    private JaxWsExecutor executor = new JaxWsCXFExecutor();

    /**
     * 实例化handler
     */
    private BeanFactory beanFactory = new SimpleBeanFactory();

    /**
     * visitor 内容的访问者
     */
    private List<JaxWsPhaseVisitor> visitors = new ArrayList<JaxWsPhaseVisitor>();

    /**
     * 用于判断是否重新reload{@linkplain JaxWsContext}
     */
    private boolean reload = true;

    /**
     * handlers配置文件所在的位置
     */
    private final String handlersLocation;

    public PropertiesFileJaxWsContextReceiver() {
        this(JAXWS_HANDLERS_LOCATION);
    }

    public PropertiesFileJaxWsContextReceiver(String handlersLocation) {
        this.handlersLocation = handlersLocation;
        this.init();
    }

    /**
     * 初始化加载{@linkplain #handlersLocation}文件中的{@linkplain JaxWsContextHandler}
     */
    protected void init() {
        try {
            Properties props = PropUtils.loadProperties(handlersLocation);
            for (String name : props.stringPropertyNames()) {
                if (!Boolean.valueOf(props.getProperty(name)))
                    continue;
                try {
                    Class<?> clazz = Class.forName(name);
                    if (JaxWsPhaseVisitor.class.isAssignableFrom(clazz)) {
                        addPhaseVisitor((JaxWsPhaseVisitor) beanFactory.getBean(clazz));
                    } else {
                        log.warn("{}不为{}的子类", clazz, JaxWsPhaseVisitor.class);
                    }
                } catch (Exception e) {
                    log.warn("无法初始化 {}", name, e);
                }
            }
        } catch (IOException e) {
            log.error("加载资源文件出错", handlersLocation, e);
        }
    }

    @Override
    public void receive(JaxWsContext context) {
        try {
            executor.execute(reloadContext(context), visitors.toArray(new JaxWsPhaseVisitor[visitors.size()]));
        } catch (Exception e) {
            log.warn("执行交互异常", context, e);
        }
    }

    @Override
    public void open() throws Exception {
        // do nothing
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }

    public boolean addPhaseVisitor(JaxWsPhaseVisitor visitor) {
        return this.visitors.add(visitor);
    }

    /**
     * 重新加载{@linkplain JaxWsContext}中的元数据
     * 
     * @param context
     * @return
     */
    protected JaxWsContext reloadContext(JaxWsContext context) {
        if (!reload && context.getAddress() != null)
            return context;
        if (metaLoader != null) {
            JaxWsMetadata metadata = metaLoader.getJaxWsMetadata(context.getServiceInterface());
            if (metadata != null) {
                SimpleJaxWsContext copyContext = new SimpleJaxWsContext(context.getServiceInterface(), context.getMethodName());
                copyContext.setParameters(context.getParameters());
                copyContext.putAll(context.getContextMap());
                copyContext.setAddress(metadata.getAddress());
                copyContext.setUsername(metadata.getUsername());
                copyContext.setPassword(metadata.getPassword());
                copyContext.setConnectionTimeout(metadata.getConnectionTimeout());
                copyContext.setReceiveTimeout(metadata.getReceiveTimeout());
                return copyContext;
            }
        }
        return context;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public void setJaxWsMetadataLoader(JaxWsMetadataLoader metaLoader) {
        this.metaLoader = metaLoader;
    }

    public void setJaxWsExecutor(JaxWsExecutor executor) {
        this.executor = executor;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}

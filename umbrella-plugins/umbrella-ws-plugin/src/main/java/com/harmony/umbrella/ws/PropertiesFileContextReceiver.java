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
package com.harmony.umbrella.ws;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.message.MessageResolver;
import com.harmony.umbrella.util.PropUtils;
import com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutor;
import com.harmony.umbrella.ws.support.AbstractJaxWsContextReceiver;
import com.harmony.umbrella.ws.support.ContextReceiver;

/**
 * 基于配置文件jaxws-receiver.txt的接收
 * 
 * @author wuxii@foxmail.com
 */
@Singleton(mappedName = "PropertiesFileContextReceiver")
@Remote({ MessageResolver.class, ContextReceiver.class })
public class PropertiesFileContextReceiver extends AbstractJaxWsContextReceiver {

    protected final static String JAXWS_HANDLERS_LOCATION = "META-INF/jaxws/jaxws-receiver.txt";

    private static final Log log = Logs.getLog(PropertiesFileContextReceiver.class);

    /**
     * 用于加载用户名密码地址
     */
    @EJB
    private MetadataLoader metadataLoader;

    /**
     * JaxWsExecutor执行者
     */
    private JaxWsExecutor executor = new JaxWsCXFExecutor();

    /**
     * handlers配置文件所在的位置
     */
    private final String propertiesFileLocation;

    private boolean init;

    public PropertiesFileContextReceiver() {
        this(JAXWS_HANDLERS_LOCATION);
    }

    public PropertiesFileContextReceiver(String handlersLocation) {
        this.propertiesFileLocation = handlersLocation;
    }

    /**
     * 初始化加载{@linkplain #propertiesFileLocation}文件中的{@linkplain PhaseVisitor}
     */
    @PostConstruct
    public void init() {
        if (!init) {
            synchronized (this) {
                if (!init) {
                    SimpleBeanFactory beanFactory = new SimpleBeanFactory();
                    Properties props = PropUtils.loadProperties(propertiesFileLocation);
                    for (String name : props.stringPropertyNames()) {
                        try {
                            Class<?> clazz = Class.forName(name);
                            if (ContextVisitor.class.isAssignableFrom(clazz)) {
                                addPhaseVisitor((ContextVisitor) beanFactory.getBean(clazz));
                            } else {
                                log.warn("{}不为{}的子类", clazz, ContextVisitor.class);
                            }
                        } catch (Exception e) {
                            log.warn("无法初始化 {}", name, e);
                        }
                    }
                    this.init = true;
                }
            }
            if (visitors.isEmpty()) {
                log.warn("properties file {} not config or not contains any phase visitor", propertiesFileLocation);
            }
        }
    }

    @Override
    protected JaxWsExecutor getJaxWsExecutor() {
        return executor;
    }

    @Override
    public void open() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    protected MetadataLoader getMetadataLoader() {
        return metadataLoader;
    }

    public void setMetadataLoader(MetadataLoader metadataLoader) {
        this.metadataLoader = metadataLoader;
    }

}

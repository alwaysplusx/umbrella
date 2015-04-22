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
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.JaxWsContextHandler;
import com.harmony.umbrella.jaxws.JaxWsExecutor;
import com.harmony.umbrella.jaxws.MetadataLoader;
import com.harmony.umbrella.jaxws.impl.JaxWsCXFExecutor;
import com.harmony.umbrella.jaxws.impl.SimpleJaxWsContext;
import com.harmony.umbrella.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PropertiesFileJaxWsContextReceiver implements JaxWsContextReceiver {

    protected final static String JAXWS_HANDLERS_LOCATION = "META-INF/jaxws/jaxws-receiver.txt";

    protected static final Logger log = LoggerFactory.getLogger(PropertiesFileJaxWsContextReceiver.class);

    /**
     * 用于加载用户名密码地址
     */
    private MetadataLoader metadataLoader;

    /**
     * 单独实例，莫要与ejb环境中的共用
     */
    private JaxWsExecutor executor = new JaxWsCXFExecutor();

    /**
     * 实例化handler
     */
    private BeanFactory beanFactory = new SimpleBeanFactory();

    /**
     * 用于判断是否重新reload{@linkplain JaxWsContext}
     */
    private boolean reload = true;

    /**
     * handlers配置文件所在的位置
     */
    private final String handlersLocation;

    /**
     * 用于判断重新设置{@linkplain JaxWsExecutor}时候是否保留远execute的handler
     */
    private boolean retentionHandlers;

    public PropertiesFileJaxWsContextReceiver() {
        this(JAXWS_HANDLERS_LOCATION);
    }

    public PropertiesFileJaxWsContextReceiver(String handlersLocation) {
        this.handlersLocation = handlersLocation;
    }

    /**
     * 初始化加载{@linkplain #handlersLocation}文件中的{@linkplain JaxWsContextHandler}
     */
    protected void init() {
        InputStream inStream = null;
        try {
            ClassLoader loader = ClassUtils.getDefaultClassLoader();
            inStream = loader.getResourceAsStream(handlersLocation);
            if (inStream != null) {
                Properties props = new Properties();
                props.load(inStream);
                initHandlers(props);
            }
        } catch (IOException e) {
            log.error("加载资源文件出错", handlersLocation, e);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                log.debug("关闭资源文件{}出错", handlersLocation, e);
            }
        }
    }

    private void initHandlers(Properties props) {
        Set<String> names = props.stringPropertyNames();
        for (String name : names) {
            if (!Boolean.valueOf(props.getProperty(name)))
                continue;
            try {
                Class<?> clazz = Class.forName(name);
                if (JaxWsContextHandler.class.isAssignableFrom(clazz)) {
                    Object handler;
                    try {
                        handler = beanFactory.getBean(clazz);
                        this.addHandler((JaxWsContextHandler) handler);
                        continue;
                    } catch (Exception e) {
                        log.error("无法初始化{}", clazz, e);
                    }
                }
                log.warn("{}不为{}的子类", clazz, JaxWsContextHandler.class);
            } catch (ClassNotFoundException e) {
                log.warn("class not find {}", name, e);
            }
        }
    }

    @Override
    public void receive(JaxWsContext context) {
        try {
            executor.execute(reloadContext(context));
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

    /**
     * 重新加载{@linkplain JaxWsContext}中的元数据
     * @param context
     * @return
     */
    protected JaxWsContext reloadContext(JaxWsContext context) {
        if (!reload && context.getAddress() != null)
            return context;
        SimpleJaxWsContext copyContext = new SimpleJaxWsContext();
        Class<?> serviceInterface = context.getServiceInterface();
        copyContext = new SimpleJaxWsContext(serviceInterface, context.getMethodName(), context.getParameters());
		if (metadataLoader != null) {
			copyContext.setAddress(metadataLoader.getAddress(serviceInterface));
			copyContext.setUsername(metadataLoader.getUsername(serviceInterface));
			copyContext.setPassword(metadataLoader.getPassword(serviceInterface));
		} else {
			copyContext.setAddress(context.getAddress());
			copyContext.setUsername(context.getUsername());
			copyContext.setPassword(context.getPassword());
		}
        copyContext.putAll(context.getContextMap());
        return copyContext;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    public boolean addHandler(JaxWsContextHandler handler) {
        return executor.addHandler(handler);
    }

    public boolean removeHandler(JaxWsContextHandler handler) {
        return executor.removeHandler(handler);
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public void setMetadataLoader(MetadataLoader metadataLoader) {
        this.metadataLoader = metadataLoader;
    }

    public void setJaxWsExecutor(JaxWsExecutor executor) {
        if (retentionHandlers) {
            List<JaxWsContextHandler> ownHandlers = this.executor.getHandlers();
            for (JaxWsContextHandler handler : ownHandlers) {
                executor.addHandler(handler);
            }
        }
        this.executor = executor;
    }

    /**
     * 是否保留原来{@linkplain JaxWsExecutor}中的的{@linkplain JaxWsContextHandler}
     */
    public boolean isRetentionHandlers() {
        return retentionHandlers;
    }

    public void setRetentionHandlers(boolean retentionHandlers) {
        this.retentionHandlers = retentionHandlers;
    }

    public void setBeanLoader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}

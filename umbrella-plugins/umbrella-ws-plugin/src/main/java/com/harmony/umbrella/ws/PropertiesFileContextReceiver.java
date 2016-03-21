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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import com.harmony.umbrella.config.ConfigurationException;
import com.harmony.umbrella.message.MessageResolver;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.PropUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutor;
import com.harmony.umbrella.ws.support.ContextReceiver;

/**
 * 基于配置文件jaxws-receiver.txt的接收
 * 
 * @author wuxii@foxmail.com
 */
@Singleton(mappedName = "PropertiesFileContextReceiver")
@Remote({ MessageResolver.class, ContextReceiver.class })
public class PropertiesFileContextReceiver extends AbstractJaxWsContextReceiver {

    protected final static String RECEIVER_CONTEXT_VISITOR_FILE_LOCATION = "META-INF/jaxws/jaxws-receiver.txt";

    private List<ContextVisitor> visitors;
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
    private final String fileLocation;

    public PropertiesFileContextReceiver() {
        this(RECEIVER_CONTEXT_VISITOR_FILE_LOCATION);
    }

    public PropertiesFileContextReceiver(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ContextVisitor[] getContextVisitor() {
        if (visitors == null) {
            Set<String> visitorClassNames = PropUtils.loadPropertiesSilently(fileLocation).stringPropertyNames();
            visitors = new ArrayList<ContextVisitor>(visitorClassNames.size());
            for (String vcn : visitorClassNames) {
                try {
                    Class<? extends ContextVisitor> visitorClass = (Class<? extends ContextVisitor>) ClassUtils.forName(vcn);
                    if (ClassFilterFeature.NEWABLE.accept(visitorClass)) {
                        visitors.add(ReflectionUtils.instantiateClass(visitorClass));
                        continue;
                    }
                } catch (Exception e) {
                    throw new ConfigurationException(e);
                }
                throw new ConfigurationException(" cannot newInstance class " + vcn);
            }
        }
        return visitors.toArray(new ContextVisitor[visitors.size()]);
    }

    @Override
    protected JaxWsExecutor getJaxWsExecutor() {
        return executor;
    }

    @Override
    protected MetadataLoader getMetadataLoader() {
        return metadataLoader;
    }

    public void setMetadataLoader(MetadataLoader metadataLoader) {
        this.metadataLoader = metadataLoader;
    }

}

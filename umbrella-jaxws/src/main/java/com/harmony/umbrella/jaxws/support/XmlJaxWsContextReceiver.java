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
package com.harmony.umbrella.jaxws.support;

import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.JaxWsExecutor;
import com.harmony.umbrella.jaxws.JaxWsMetadata;
import com.harmony.umbrella.jaxws.JaxWsMetadataLoader;
import com.harmony.umbrella.jaxws.JaxWsPhaseVisitor;
import com.harmony.umbrella.jaxws.impl.SimpleJaxWsContext;

/**
 * for spring config
 * 
 * @author wuxii@foxmail.com
 */
public class XmlJaxWsContextReceiver implements JaxWsContextReceiver {

    private JaxWsExecutor executor;

    private JaxWsMetadataLoader metaLoader;

    private boolean reload = true;

    private List<JaxWsPhaseVisitor> visitors = new ArrayList<JaxWsPhaseVisitor>();

    public XmlJaxWsContextReceiver() {
    }

    public XmlJaxWsContextReceiver(JaxWsExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void receive(JaxWsContext context) {
        executor.execute(reload(context), visitors.toArray(new JaxWsPhaseVisitor[visitors.size()]));
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

    protected JaxWsContext reload(JaxWsContext context) {
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

    public JaxWsExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(JaxWsExecutor executor) {
        this.executor = executor;
    }

    public JaxWsMetadataLoader getMetaLoader() {
        return metaLoader;
    }

    public void setMetaLoader(JaxWsMetadataLoader metaLoader) {
        this.metaLoader = metaLoader;
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public List<JaxWsPhaseVisitor> getVisitors() {
        return visitors;
    }

    public void setVisitors(List<JaxWsPhaseVisitor> visitors) {
        this.visitors = visitors;
    }

}

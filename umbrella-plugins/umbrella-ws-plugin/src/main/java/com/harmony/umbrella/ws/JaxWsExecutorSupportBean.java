/*
 * Copyright 2002-2015 the original author or authors.
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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutorSupport;
import com.harmony.umbrella.ws.support.ContextSender;
import com.harmony.umbrella.ws.support.SimpleContext;

/**
 * JaxWs 执行者的发送context扩展
 * 
 * @author wuxii@foxmail.com
 */
@Remote({ JaxWsExecutorSupport.class })
@Stateless(mappedName = "JaxWsExecutorSupportBean")
public class JaxWsExecutorSupportBean extends JaxWsCXFExecutor implements JaxWsExecutorSupport {

    @EJB(mappedName = "ContextSenderBean")
    private ContextSender contextSender;

    @EJB
    private MetadataLoader metadataLoader;

    private boolean reload;

    @PostConstruct
    public void postConstruct() {

    }

    @Override
    public boolean send(Context context) {
        return contextSender.send(reloadContext(context));
    }

    protected Context reloadContext(Context context) {
        final MetadataLoader loader = metadataLoader;
        if (loader != null && reload) {
            Metadata metadata = loader.loadMetadata(context.getServiceInterface());
            if (metadata != null) {
                SimpleContext copyContext = new SimpleContext(context.getServiceInterface(), context.getMethodName());
                copyContext.setAddress(metadata.getAddress());
                copyContext.setUsername(metadata.getUsername());
                copyContext.setPassword(metadata.getPassword());
                copyContext.setConnectionTimeout(metadata.getConnectionTimeout());
                copyContext.setReceiveTimeout(metadata.getReceiveTimeout());

                copyContext.setParameters(context.getParameters());
                copyContext.putAll(context.getContextMap());
                return copyContext;
            }
        }
        return context;
    }

}

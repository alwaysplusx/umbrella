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

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceException;

import com.harmony.umbrella.message.MessageException;
import com.harmony.umbrella.message.jms.JmsMessageSender;
import com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutorSupport;
import com.harmony.umbrella.ws.support.ContextSender;

/**
 * JaxWs 执行者的发送context扩展
 * 
 * @author wuxii@foxmail.com
 */
@Remote({ JaxWsExecutorSupport.class })
@Stateless(mappedName = "JaxWsExecutorSupportBean")
public class JaxWsExecutorAndSenderSupportBean extends JaxWsCXFExecutor implements JaxWsExecutorSupport, ContextSender {

    @EJB(mappedName = JmsMessageSender.DEFAULT_MESSAGE_SENDER_MAPPEDNAME)
    private JmsMessageSender messageSender;
    @EJB
    private MetadataLoader metadataLoader;

    private boolean reload;

    @Override
    public boolean send(Context context) {
        context = reloadContext(context);
        try {
            return messageSender.send(new ContextMessage(context));
        } catch (MessageException e) {
            throw new WebServiceException("cannot send webservice context", e);
        }
    }

    protected Context reloadContext(Context context) {
        final MetadataLoader loader = metadataLoader;
        if (loader != null && reload) {
            Metadata metadata = loader.loadMetadata(context.getServiceInterface());
            if (metadata != null) {
                context = ContextUtils.reset(context, metadata);
            }
        }
        return context;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

}

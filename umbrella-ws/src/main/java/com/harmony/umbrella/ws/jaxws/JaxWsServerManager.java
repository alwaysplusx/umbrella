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

import javax.xml.ws.WebServiceException;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.ws.FactoryConfig;
import com.harmony.umbrella.ws.Server;
import com.harmony.umbrella.ws.ServerManager;
import com.harmony.umbrella.ws.cxf.ServerImpl;
import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsServerManager extends ServerManager<JaxWsProxyFactoryBean> {

    private static final Log log = Logs.getLog(JaxWsServerManager.class);

    private static JaxWsServerManager INSTANCE;

    private JaxWsServerManager() {
    }

    public static JaxWsServerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (JaxWsServerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JaxWsServerManager();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected Server doPublish(Class<?> serviceClass, Object serviceBean, String address, FactoryConfig<JaxWsProxyFactoryBean> factoryConfig) {
        Assert.isTrue(serviceClass != null || serviceBean != null, "service bean and service class are null");

        try {
            JaxWsServerBuilder builder = JaxWsServerBuilder//
                    .create()//
                    .setServiceClass(serviceClass)//
                    .setServiceBean(serviceBean)//
                    .setAddress(address);

            if (log.isInfoEnabled()) {
                builder.addInInterceptor(new MessageInInterceptor("JaxWs-Server Inbound"));
                builder.addOutInterceptor(new MessageOutInterceptor("JaxWs-Server Outbound"));
            }

            return new ServerImpl(serviceClass, serviceBean, builder.publish(factoryConfig));

        } catch (Exception e) {
            throw new WebServiceException("publish server failed!", e);
        }
    }

}

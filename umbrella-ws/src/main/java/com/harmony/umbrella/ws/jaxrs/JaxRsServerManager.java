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
package com.harmony.umbrella.ws.jaxrs;

import javax.xml.ws.WebServiceException;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class JaxRsServerManager extends ServerManager<JAXRSServerFactoryBean> {

    private static final Logger log = LoggerFactory.getLogger(JaxRsServerManager.class);

    private static JaxRsServerManager INSTANCE;

    private JaxRsServerManager() {
    }

    public static JaxRsServerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (JaxRsServerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JaxRsServerManager();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected Server doPublish(Class<?> serviceClass, Object serviceBean, String address, FactoryConfig<JAXRSServerFactoryBean> factoryConfig) {
        Assert.isTrue(serviceClass != null || serviceBean != null, "service bean and service class are null");

        try {
            JaxRsServerBuilder builder = JaxRsServerBuilder//
                    .create()//
                    .setResourceClass(serviceClass)//
                    .setResourceBean(serviceBean)//
                    .setAddress(address);

            if (log.isInfoEnabled()) {
                builder.addInInterceptor(new MessageInInterceptor("JaxWs-Server Inbound"));
                builder.addOutInterceptor(new MessageOutInterceptor("JaxWs-Server Outbound"));
            }

            return new ServerImpl(serviceClass, serviceBean, builder.publish(factoryConfig));

        } catch (Exception e) {
            throw new WebServiceException("publish server " + serviceBean.getClass() + " failed!", e);
        }
    }

}

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
package com.harmony.umbrella.ws.cxf;

import com.harmony.umbrella.ws.Server;

/**
 * @author wuxii@foxmail.com
 */
public class ServerImpl implements Server {

    private Class<?> serviceClass;
    private Object serviceBean;
    private org.apache.cxf.endpoint.Server cxfServer;

    public ServerImpl(Class<?> serviceClass, Object serviceBean, org.apache.cxf.endpoint.Server server) {
        this.serviceClass = serviceClass;
        this.serviceBean = serviceBean;
        this.cxfServer = server;
    }

    @Override
    public Object getServiceBean() {
        return serviceBean;
    }

    @Override
    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public org.apache.cxf.endpoint.Server getCXFServer() {
        return cxfServer;
    }

}

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

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.transport.Destination;

import com.harmony.umbrella.ws.ServerManager;

/**
 * @author wuxii@foxmail.com
 */
public class ServerImpl implements Server {

    private final Class<?> clazz;
    private final Server server;
    private final ServerType serverType;

    public ServerImpl(Class<?> clazz, Server server) {
        this.clazz = clazz;
        this.server = server;
        this.serverType = ServerType.fromClass(clazz);
    }

    public Class<?> getServiceClass() {
        return clazz;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public boolean isJaxWs() {
        return serverType == ServerType.JAXWS;
    }

    public boolean isJaxRs() {
        return serverType == ServerType.JAXRS;
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.stop();
    }

    @Override
    public void destroy() {
        server.destroy();
    }

    @Override
    public boolean isStarted() {
        return server.isStarted();
    }

    @Override
    public Destination getDestination() {
        return server.getDestination();
    }

    @Override
    public Endpoint getEndpoint() {
        return server.getEndpoint();
    }

    public enum ServerType {
        NONE, JAXWS, JAXRS;
        public static ServerType fromClass(Class<?> clazz) {
            if (ServerManager.isJaxRsService(clazz)) {
                return JAXRS;
            } else if (ServerManager.isJaxWsService(clazz)) {
                return JAXWS;
            }
            return NONE;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + ((serverType == null) ? 0 : serverType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServerImpl other = (ServerImpl) obj;
        if (clazz == null) {
            if (other.clazz != null)
                return false;
        } else if (!clazz.equals(other.clazz))
            return false;
        if (serverType != other.serverType)
            return false;
        return true;
    }

}

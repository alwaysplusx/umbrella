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

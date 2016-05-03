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

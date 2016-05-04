package com.harmony.umbrella.plugin.ws;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceException;

import com.harmony.umbrella.message.MessageException;
import com.harmony.umbrella.message.jms.JmsMessageSender;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutorSupport;

/**
 * JaxWs 执行者的发送context扩展
 * 
 * @author wuxii@foxmail.com
 */
@Remote({ JaxWsExecutorSupport.class })
@Stateless(mappedName = "ExecutorSupportBean")
public class JaxWsExecutorSupportBean extends JaxWsCXFExecutor implements JaxWsExecutorSupport {

    @EJB
    private JmsMessageSender messageSender;

    @EJB
    private MetadataLoader metadataLoader;

    private boolean reload = false;

    @Override
    public boolean send(Context context) {
        context = reload(context);
        try {
            return messageSender.send(new ContextMessage(context));
        } catch (MessageException e) {
            throw new WebServiceException("cannot send webservice context", e);
        }
    }

    protected Context reload(Context context) {
        final MetadataLoader loader = metadataLoader;
        if (loader != null && reload) {
            Metadata metadata = loader.loadMetadata(context.getServiceInterface());
            return new ContextWrapper(context, metadata);
        }
        return context;
    }

    /*@SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Class<T> serviceInterface) {
        Metadata metadata = metadataLoader.loadMetadata(serviceInterface.getName());
        return (T) getProxy(metadata);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(String serviceName) {
        try {
            return (T) getProxy(ClassUtils.forName(serviceName));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }*/

}

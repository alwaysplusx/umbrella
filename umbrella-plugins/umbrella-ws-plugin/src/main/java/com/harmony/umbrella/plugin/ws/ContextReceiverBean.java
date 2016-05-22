package com.harmony.umbrella.plugin.ws;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.MessageResolver;
import com.harmony.umbrella.ws.ContextVisitor;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutor;
import com.harmony.umbrella.ws.support.ContextReceiver;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "ContextReceiverBean")
@Remote({ MessageResolver.class, ContextReceiver.class })
public class ContextReceiverBean extends AbstractContextReceiver /*implements ContextReceiver, MessageResolver*/{

    public static final String MetadataLoader = ContextReceiver.class.getSimpleName() + "MessageLoader";

    public static final String ContextVisitor = ContextReceiver.class.getSimpleName() + "ContextVisitor";

    @EJB
    private Configurations config;

    private JaxWsExecutor executor = new JaxWsCXFExecutor();

    @Override
    protected ContextVisitor[] getContextVisitor() {
        List<ContextVisitor> visitors = config.getBeans(ContextVisitor);
        return visitors.toArray(new ContextVisitor[visitors.size()]);
    }

    @Override
    protected JaxWsExecutor getJaxWsExecutor() {
        return executor;
    }

    @Override
    protected MetadataLoader getMetadataLoader() {
        return config.getBean(ContextReceiverBean.class.getName() + ".MetadataLoader");
    }

}

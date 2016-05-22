package com.harmony.umbrella.plugin.message;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.MessageConsumer;
import com.harmony.umbrella.message.jms.AbstractJmsMessageConsumer;
import com.harmony.umbrella.message.jms.JmsMessageConsumer;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "ApplicationMessageConsumer")
@Remote({ MessageConsumer.class, JmsMessageConsumer.class })
public class ApplicationMessageConsumer extends AbstractJmsMessageConsumer {

    @EJB
    private Configurations configurations;

    @Override
    protected ConnectionFactory getConnectionFactory() {
        return configurations.getBean(ApplicationMessageConstants.applicationConnectionFactory);
    }

    @Override
    protected Destination getDestination() {
        return configurations.getBean(ApplicationMessageConstants.applicationDestination);
    }

}

package com.harmony.umbrella.plugin.message;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.MessageSender;
import com.harmony.umbrella.message.jms.AbstractJmsMessageSender;
import com.harmony.umbrella.message.jms.JmsMessageSender;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "ApplicationMessageSender")
@Remote({ MessageSender.class, JmsMessageSender.class })
public class ApplicationMessageSender extends AbstractJmsMessageSender {

    @EJB
    private Configurations config;

    @Override
    protected ConnectionFactory getConnectionFactory() {
        return config.getBean(ApplicationMessageConstants.applicationConnectionFactory);
    }

    @Override
    protected Destination getDestination() {
        return config.getBean(ApplicationMessageConstants.applicationDestination);
    }

}

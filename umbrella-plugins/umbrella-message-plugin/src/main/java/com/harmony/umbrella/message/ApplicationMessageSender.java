package com.harmony.umbrella.message;

import static com.harmony.umbrella.config.Configurations.*;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.jms.AbstractJmsMessageSender;
import com.harmony.umbrella.message.jms.JmsMessageSender;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "ApplicationMessageSender")
@Remote({ MessageSender.class, JmsMessageSender.class })
public class ApplicationMessageSender extends AbstractJmsMessageSender {

    public static final String ApplicationDestination = "applicationDestination";
    public static final String ApplicationConnectionFactory = "applicationConnectionFactory";

    @EJB(mappedName = APPLICATION_CONFIGURATIONS, beanName = APPLICATION_CONFIGURATIONS)
    private Configurations config;

    @Override
    protected ConnectionFactory getConnectionFactory() {
        return config.getBean(ApplicationConnectionFactory);
    }

    @Override
    protected Destination getDestination() {
        return config.getBean(ApplicationDestination);
    }

}

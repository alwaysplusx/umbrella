package com.harmony.umbrella.message;

import static com.harmony.umbrella.message.ApplicationMessageConstants.*;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.message.jms.AbstractJmsMessageConsumer;
import com.harmony.umbrella.message.jms.JmsMessageConsumer;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "ApplicationMessageConsumer")
@Remote({ MessageConsumer.class, JmsMessageConsumer.class })
public class ApplicationMessageConsumer extends AbstractJmsMessageConsumer {

    @Resource(name = CONNECTION_FACTORY_NAME)
    private ConnectionFactory connectionFactory;
    @Resource(name = QUEUE_NAME)
    private Destination destination;

    @Override
    protected ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    protected Destination getDestination() {
        return destination;
    }

}

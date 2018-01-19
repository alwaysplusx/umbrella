package com.harmony.umbrella.message;

import javax.jms.Session;

/**
 * @author wuxii@foxmail.com
 */
public class MessageConfiguration {

    // connectionFactory
    protected String username;
    protected String password;

    // session & JMSContext
    protected boolean transacted = true;
    protected int sessionMode = Session.AUTO_ACKNOWLEDGE;

    // JMSContext
    protected boolean jmsContextAutoStart = true;

    protected long receiveTimeout;

    // message producer
    protected int deliveryMode;
    protected int priority;
    protected long timeToLive;
    protected long deliveryDelay;

    protected boolean sessionAutoCommit = true;
    protected boolean autoStartListener = true;

}

package com.harmony.umbrella.message.support;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;

/**
 * @author wuxii@foxmail.com
 */
public class JMSContextFacade {

    private JMSContext context;
    protected boolean autoCommit;
    protected boolean transacted;

    public JMSContextFacade(JMSContext context, boolean autoCommit) {
        this.context = context;
        this.autoCommit = autoCommit;
    }

    public void sendMessage(Message message) {
        JMSProducer producer = context.createProducer();
        producer.send(null, message);
    }

    public JMSContext getJMSContext() {
        return context;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

}

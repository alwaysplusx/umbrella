package com.harmony.umbrella.message;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.JMSProducer;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageTemplateV2 extends MessageTemplate {

    void sendMessage(Destination dest, MessageCreator creator, JMSProducerConfigure configure) throws JMSException;

    public interface JMSProducerConfigure {

        void config(JMSProducer producer);

    }

}

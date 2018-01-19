package com.harmony.umbrella.message.v2;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;

import com.harmony.umbrella.message.MessageHelper;
import com.harmony.umbrella.message.MessageTemplateV2;

/**
 * @author wuxii@foxmail.com
 */
public abstract class JMSContextMessageTemplate extends MessageHelper implements MessageTemplateV2 {

    @Override
    public void sendMessage(Destination destination, MessageCreator messageCreator, MessageProducerConfigure configure) throws JMSException {
        if (configure == null) {

        } else {
            super.sendMessage(destination, messageCreator, configure);
        }
    }

    @Override
    public void sendMessage(Destination dest, MessageCreator creator, JMSProducerConfigure configure) throws JMSException {
        JMSContext jmsContext = getJmsTemplate().createJmsContext();
        JMSProducer producer = jmsContext.createProducer();
        Message message = creator.newMessage(jmsContext);
        if (configure != null) {
            configure.config(producer);
        }
        producer.send(dest, message);
    }

}

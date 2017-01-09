package com.harmony.umbrella.message.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import com.harmony.umbrella.message.MessageProducer;

/**
 * jms消费生产扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface JmsMessageProducer extends MessageProducer {

    <T extends Message> void send(MessageConfiger<T> messageConfiger) throws JMSException;

}

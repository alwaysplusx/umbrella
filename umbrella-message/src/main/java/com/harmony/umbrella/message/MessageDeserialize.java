package com.harmony.umbrella.message;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageDeserialize {

    void deserialize(Message message, Serializable content) throws JMSException;

}

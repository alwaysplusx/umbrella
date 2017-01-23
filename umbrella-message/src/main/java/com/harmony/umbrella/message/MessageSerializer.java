package com.harmony.umbrella.message;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageSerializer {

    Serializable serialize(Message message) throws JMSException;

}

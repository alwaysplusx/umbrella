package com.harmony.umbrella.message.jms;

import javax.jms.Message;
import javax.jms.Session;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageConfiger<T extends Message> {

    T create(Session session);

    void config(T message);

}

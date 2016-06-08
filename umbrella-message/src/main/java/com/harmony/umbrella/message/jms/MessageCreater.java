package com.harmony.umbrella.message.jms;

import javax.jms.Message;
import javax.jms.Session;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageCreater {

    Message createMessage(Session session);

}

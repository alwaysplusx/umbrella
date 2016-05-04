package com.harmony.umbrella.plugin.ws;

import javax.xml.ws.WebServiceException;

import com.harmony.umbrella.message.MessageException;
import com.harmony.umbrella.message.MessageSender;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.support.ContextSender;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractContextSender implements ContextSender {

    protected abstract MessageSender getMessageSender();

    @Override
    public boolean send(Context context) {
        return send(new ContextMessage(context));
    }

    protected boolean send(ContextMessage contextMessage) {
        try {
            return getMessageSender().send(contextMessage);
        } catch (MessageException e) {
            throw new WebServiceException(e);
        }
    }

}

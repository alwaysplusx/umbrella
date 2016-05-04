package com.harmony.umbrella.plugin.ws;

import static com.harmony.umbrella.config.Configurations.*;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.MessageSender;
import com.harmony.umbrella.ws.support.ContextSender;

/**
 * @author wuxii@foxmail.com
 */
@Remote(ContextSender.class)
@Stateless(mappedName = "ContextSenderBean")
public class ContextSenderBean extends AbstractContextSender {

    public static final String MessageSender = ContextSender.class.getSimpleName() + "MessageSender";

    @EJB(mappedName = APPLICATION_CONFIGURATIONS, beanName = APPLICATION_CONFIGURATIONS)
    private Configurations config;

    @Override
    protected MessageSender getMessageSender() {
        return config.getBean(MessageSender);
    }

}

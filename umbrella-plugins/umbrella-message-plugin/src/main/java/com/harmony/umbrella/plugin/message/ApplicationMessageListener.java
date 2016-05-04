package com.harmony.umbrella.plugin.message;

import static com.harmony.umbrella.plugin.message.ApplicationMessageConstants.*;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Remote;
import javax.jms.MessageListener;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.MessageResolver;
import com.harmony.umbrella.message.jms.AbstractJmsMessageListener;

/**
 * @author wuxii@foxmail.com
 */
@MessageDriven(mappedName = QUEUE_NAME, activationConfig = { 
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
@Remote({ MessageListener.class, com.harmony.umbrella.message.MessageListener.class })
public class ApplicationMessageListener extends AbstractJmsMessageListener implements MessageListener {

    @EJB(mappedName = Configurations.APPLICATION_CONFIGURATIONS)
    private Configurations configurations;

    public static final String ApplicationMessageResolver = "applicationMessageResolver";

    @EJB
    private MessageResolver resolver;

    @Override
    public void init() {
    }

    @Override
    protected List<MessageResolver> getMessageResolvers() {
        return configurations.getBeans(ApplicationMessageResolver);
    }

    @Override
    public void destroy() {
    }

}

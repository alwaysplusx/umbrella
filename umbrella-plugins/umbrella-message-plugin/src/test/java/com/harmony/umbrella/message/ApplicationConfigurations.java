package com.harmony.umbrella.message;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.config.ConfigSupport;
import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.config.annotation.Bean;

/**
 * @author wuxii@foxmail.com
 */
@Stateless
@Remote({ Configurations.class })
public class ApplicationConfigurations extends ConfigSupport {

    @EJB(mappedName = "ApplicationMessageMessageResolver")
    private MessageResolver resolver;

    @Resource
    private ConnectionFactory connectionFactory;

    @Resource
    private Destination destination;

    @Bean
    public List<MessageResolver> applicationMessageListenerMessageResolvers() {
        return Arrays.asList(resolver);
    }

    @Bean
    public ConnectionFactory applicationConnectionFactory() {
        return connectionFactory;
    }

    @Bean
    public Destination applicationDestination() {
        return destination;
    }

}

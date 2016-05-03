package com.harmony.umbrella.message;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.config.ConfigSupport;
import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.config.annotation.Bean;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = Configurations.APPLICATION_CONFIGURATIONS)
@Remote({ Configurations.class })
public class ApplicationConfigurations extends ConfigSupport {

    @EJB(mappedName = "ApplicationMessageMessageResolver")
    private MessageResolver resolver;

    @Bean
    public List<MessageResolver> applicationMessageListenerMessageResolvers() {
        return Arrays.asList(resolver);
    }

}

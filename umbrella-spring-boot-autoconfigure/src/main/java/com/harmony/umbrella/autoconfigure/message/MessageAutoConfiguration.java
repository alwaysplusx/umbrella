package com.harmony.umbrella.autoconfigure.message;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.harmony.umbrella.message.MessageTemplateFactory;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnProperty(prefix = "harmony.message", value = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MessageProperties.class)
public class MessageAutoConfiguration {

    @Configuration
    @ConditionalOnClass(MessageTemplateFactory.class)
    @ConditionalOnProperty(prefix = "harmony.message.templates", value = "enabled", havingValue = "true", matchIfMissing = true)
    @EnableConfigurationProperties(MessageProperties.class)
    public class MessageTemplateAutoConfiguration {

        @Bean
        public MessageTemplateFactory messageTemplateFactory() {
            return null;
        }

    }

    public class BrokerAutoConfiguration {

    }

}

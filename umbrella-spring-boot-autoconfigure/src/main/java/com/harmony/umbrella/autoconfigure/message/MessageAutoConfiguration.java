package com.harmony.umbrella.autoconfigure.message;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnProperty(prefix = "harmony.message", value = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MessageProperties.class)
public class MessageAutoConfiguration {

}

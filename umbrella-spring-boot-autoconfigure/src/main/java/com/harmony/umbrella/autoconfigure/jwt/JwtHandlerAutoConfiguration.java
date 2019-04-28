package com.harmony.umbrella.autoconfigure.jwt;

import com.auth0.jwt.JWT;
import com.harmony.umbrella.security.jwt.JwtTokenHandler;
import com.harmony.umbrella.security.jwt.support.Auth0JwtTokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author wuxii
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Auth0JwtTokenHandler.class, JWT.class})
@EnableConfigurationProperties(JwtHandlerProperties.class)
public class JwtHandlerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JwtHandlerAutoConfiguration.class);

    private final JwtHandlerProperties jwtAuthenticationProperties;

    public JwtHandlerAutoConfiguration(JwtHandlerProperties jwtAuthenticationProperties) {
        this.jwtAuthenticationProperties = jwtAuthenticationProperties;
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(JwtTokenHandler.class)
    public JwtTokenHandler jwtTokenHandler() {
        String signature = jwtAuthenticationProperties.getSignature();
        if (!StringUtils.hasText(signature)) {
            signature = UUID.randomUUID().toString();
            log.warn("jwt token signature not been set, auto generate it {}", signature);
        }
        Auth0JwtTokenHandler jwtTokenHandler = new Auth0JwtTokenHandler(signature);
        jwtTokenHandler.setExpiresIn((int) jwtAuthenticationProperties.getExpiresIn().getSeconds());
        jwtTokenHandler.setIssuer(jwtAuthenticationProperties.getIssuer());
        return jwtTokenHandler;
    }

}

package com.harmony.umbrella.autoconfigure.web;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.ApplicationConfigurationBuilder;
import com.harmony.umbrella.web.context.WebApplicationSpringInitializer;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(WebApplicationSpringInitializer.class)
@ConditionalOnProperty(prefix = "harmony.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebAppProperties.class)
public class WebAppConfiguration {

    private final WebAppProperties webAppProperties;
    private final ServletContext servletContext;

    public WebAppConfiguration(WebAppProperties webAppProperties, ServletContext servletContext) {
        this.servletContext = servletContext;
        this.webAppProperties = webAppProperties;
    }

    private ApplicationConfiguration appConfig() throws NamingException, ServletException {
        ApplicationConfigurationBuilder builder = ApplicationConfigurationBuilder.create();
        builder.apply(servletContext);
        if (webAppProperties.getDatasources() != null) {
            for (String jndi : webAppProperties.getDatasources()) {
                builder.addDataSource(jndi);
            }
        }
        if (webAppProperties.getInitializer() != null) {
            builder.setApplicationContextInitializer(webAppProperties.getInitializer());
        }
        if (webAppProperties.getScanPackages() != null) {
            for (String pkg : webAppProperties.getScanPackages()) {
                builder.addScanPackage(pkg);
            }
        }
        if (webAppProperties.getShutdownHooks() != null) {
            for (Class<? extends Runnable> cls : webAppProperties.getShutdownHooks()) {
                builder.addShutdownHook(cls);
            }
        }
        return builder.build();
    }

    @Bean
    WebApplicationInitializer webAppInitializer() throws NamingException, ServletException {
        WebApplicationSpringInitializer initializer = new WebApplicationSpringInitializer();
        initializer.setApplicationConfiguration(appConfig());
        return initializer;
    }

}

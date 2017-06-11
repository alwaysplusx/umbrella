package com.harmony.umbrella.autoconfigure.web;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.ApplicationConfigurationBuilder;
import com.harmony.umbrella.context.WebXmlConstant;
import com.harmony.umbrella.web.context.WebApplicationSpringInitializer;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(WebApplicationSpringInitializer.class)
@EnableConfigurationProperties(WebAppProperties.class)
public class WebAppAutoConfiguration {

    private final WebAppProperties webAppProperties;
    private final ServletContext servletContext;

    public WebAppAutoConfiguration(WebAppProperties webAppProperties, ServletContext servletContext) {
        this.servletContext = servletContext;
        this.webAppProperties = webAppProperties;
    }

    private ApplicationConfiguration appConfig() throws NamingException, ServletException {
        ApplicationConfigurationBuilder builder = ApplicationConfigurationBuilder.create();
        if (servletContext != null) {
            builder.apply(servletContext);
        }
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

        Properties properties = webAppProperties.getProperties();
        Iterator<Entry<Object, Object>> it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Object, Object> entry = it.next();
            builder.addProperty(WebXmlConstant.APPLICATION_CFG_PROPERTIES + "." + entry.getKey().toString(), entry.getValue());
        }

        return builder.build();
    }

    @Bean
    ServletContextInitializer webAppInitializer() throws NamingException, ServletException {
        WebApplicationSpringInitializer initializer = new WebApplicationSpringInitializer();
        initializer.setApplicationConfiguration(appConfig());
        return new ServletContextInitializer() {

            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                initializer.onStartup(servletContext);
            }

        };
    }

}

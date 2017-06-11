package com.harmony.umbrella.autoconfigure.ejb;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import com.harmony.umbrella.core.PropertyManager;
import com.harmony.umbrella.core.SimplePropertyManager;
import com.harmony.umbrella.ee.BeanResolver;
import com.harmony.umbrella.ee.EJBApplicationContext;
import com.harmony.umbrella.ee.formatter.JndiFormatter;
import com.harmony.umbrella.ee.support.PartResolver;
import com.harmony.umbrella.util.PropertiesUtils;

/**
 * FIXME 配置优化
 * 
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(EJBApplicationContext.class)
@ConditionalOnProperty(prefix = "harmony.ejb", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(EJBProperties.class)
public class EJBAutoConfiguration {

    private final EJBProperties ejbProperties;

    public EJBAutoConfiguration(EJBProperties ejbProperties) {
        this.ejbProperties = ejbProperties;
    }

    @Bean
    public PropertyManager propertyManager() throws IOException {
        String location = ejbProperties.getContextPropertiesFileLocation();
        Properties props = null;
        if (location != null) {
            props = PropertiesUtils.loadProperties(location);
        }
        if (ejbProperties.getContextProperties() != null) {
            props.putAll(ejbProperties.getContextProperties());
        }
        return new SimplePropertyManager(props);
    }

    @Bean
    PartResolver beanInterfaceResolver() {
        return null;
    }

    @Bean
    PartResolver beanNameResolver() {
        return null;
    }

    @Bean
    BeanResolver beanResolver() {
        return null;
    }

    @Bean
    JndiFormatter jndiFormatter() {
        return null;
    }

    @Bean
    EJBBeanFactory ejbBeanFactory() {
        return null;
    }

    @Bean(AnnotationConfigUtils.COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)
    public CommonAnnotationBeanPostProcessor overrideCommonAnnotationProcessor() throws IOException {
        CommonAnnotationBeanPostProcessor result = new CommonAnnotationBeanPostProcessor();
        result.setJndiFactory(ejbBeanFactory());
        return result;
    }

    /**
     * @author wuxii@foxmail.com
     */
    protected abstract static class EJBBeanFactory implements BeanFactory {

        com.harmony.umbrella.core.BeanFactory beanFactory;

    }

}

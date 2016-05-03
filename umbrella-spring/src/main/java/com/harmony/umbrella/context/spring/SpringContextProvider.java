package com.harmony.umbrella.context.spring;

import java.net.URL;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ContextProvider;

/**
 * @author wuxii@foxmail.com
 */
public class SpringContextProvider extends ContextProvider {

    @Override
    public ApplicationContext createApplicationContext() {
        return createApplicationContext(null    );
    }

    @Override
    public ApplicationContext createApplicationContext(URL url) {
        Assert.notNull(SpringContextHolder.springApplication, "spring application context not set, configuration spring bean class" + SpringContextHolder.class);
        return new SpringApplicationContext(SpringContextHolder.springApplication, url);
    }

    public static class SpringContextHolder implements ApplicationContextAware {

        private static org.springframework.context.ApplicationContext springApplication;

        @Override
        public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
            if (springApplication == null) {
                SpringContextHolder.springApplication = applicationContext;
            }
        }

    }

}

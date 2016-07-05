package com.harmony.umbrella.context.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ContextProvider;

/**
 * @author wuxii@foxmail.com
 */
public class SpringContextProvider implements ContextProvider {

    @Override
    public ApplicationContext createApplicationContext() {
        return new SpringApplicationContext(SpringContextHolder.springApplication, null);
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

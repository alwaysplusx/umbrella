package com.harmony.umbrella.context.spring;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.core.BeanFactory;

/**
 * @author wuxii@foxmail.com
 */
public class SpringApplicationContext extends ApplicationContext {

    private SpringBeanFactory beanFactory;

    public SpringApplicationContext(org.springframework.context.ApplicationContext springContext) {
        this.beanFactory = new SpringBeanFactory(springContext);
    }

    @Override
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

}

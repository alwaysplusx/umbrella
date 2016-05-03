package com.harmony.umbrella.context;

import java.net.URL;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.core.SimpleBeanFactory;

/**
 * 应用上下文的provider. 让应用可以在使用时候选择创建何种的应用环境
 * 
 * @author wuxii@foxmail.com
 */
public class ContextProvider {

    public static final ContextProvider INSTANCE = new ContextProvider();

    public ApplicationContext createApplicationContext() {
        return createApplicationContext(null);
    }

    /**
     * 创建应用上下文
     *
     * @return 应用上下文
     */
    public ApplicationContext createApplicationContext(URL url) {
        return new SimpleApplicationContext();
    }

    private static final class SimpleApplicationContext extends ApplicationContext {

        private BeanFactory beanFactory = SimpleBeanFactory.INSTANCE;

        @Override
        public <T> T getBean(String beanName) throws NoSuchBeanFoundException {
            return beanFactory.getBean(beanName);
        }

        @Override
        public <T> T getBean(String beanName, String scope) throws NoSuchBeanFoundException {
            return beanFactory.getBean(beanName, scope);
        }

        @Override
        public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFoundException {
            return beanFactory.getBean(beanClass);
        }

        @Override
        public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFoundException {
            return beanFactory.getBean(beanClass, scope);
        }

        @Override
        public void init() {
        }

        @Override
        public void destroy() {
        }

    }

}

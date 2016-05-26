package com.harmony.umbrella.context;

import java.util.ServiceLoader;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * 运行的应用的上下文
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

    protected static final Log LOG = Logs.getLog(ApplicationContext.class);

    protected static final ThreadLocal<CurrentContext> current = new InheritableThreadLocal<CurrentContext>();

    /**
     * 获取当前应用的应用上下文
     * <p>
     * 加载 {@code META-INF/services/com.harmony.umbrella.context.spi.ApplicationContextProvider} 文件中的实际类型来创建
     *
     * @return 应用上下文
     */
    public static final ApplicationContext getApplicationContext() {
        return getApplicationContext0();
    }

    /**
     * 获取当前应用的上下文, 并使用初始化属性{@code props}对应用进行初始化
     *
     * @param url
     *            配置文件url
     * @return 应用上下文
     */
    private static final ApplicationContext getApplicationContext0() {
        ApplicationContext context = null;
        ServiceLoader<ContextProvider> providers = ServiceLoader.load(ContextProvider.class);
        for (ContextProvider provider : providers) {
            try {
                context = provider.createApplicationContext();
                if (context != null) {
                    LOG.debug("create context [{}] by [{}]", context, provider);
                    break;
                }
            } catch (Exception e) {
                LOG.warn("", e);
            }
        }
        if (context == null) {
            context = SimpleApplicationContext.SIMPLE_APPLICATION_CONTEXT;
            LOG.warn("no context provider find, use default {}", SimpleApplicationContext.class.getName());
        }
        // 初始化
        context.init();
        return context;
    }

    /**
     * 初始化应用上下文
     */
    public abstract void init();

    /**
     * 销毁应用上下文
     */
    public abstract void destroy();

    /**
     * 判断是否存在用户上下文
     *
     * @return
     */
    public boolean hasCurrentContext() {
        return current.get() != null;
    }

    /**
     * 设置当前线程的用户环境
     *
     * @param currentCtx
     *            用户环境
     */
    public void setCurrentContext(CurrentContext cc) {
        current.set(cc);
    }

    /**
     * 移除当前的CurrentContext
     * 
     * @return 当前的current context
     */
    public CurrentContext removeCurrentContext() {
        CurrentContext cc = this.getCurrentContext();
        current.set(null);
        return cc;
    }

    /**
     * 获取当前线程的用户环境
     *
     * @return 用户环境
     */
    public CurrentContext getCurrentContext() {
        return current.get();
    }

    private static final class SimpleApplicationContext extends ApplicationContext {

        private static final SimpleApplicationContext SIMPLE_APPLICATION_CONTEXT = new SimpleApplicationContext();

        private BeanFactory beanFactory = new SimpleBeanFactory();

        @Override
        public void init() {
        }

        @Override
        public void destroy() {
        }

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

    }
}

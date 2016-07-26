package com.harmony.umbrella.context;

import java.util.ServiceLoader;

import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.BeansException;
import com.harmony.umbrella.beans.SimpleBeanFactory;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
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

    static ServerMetadata serverMetadata = ApplicationMetadata.EMPTY_SERVER_METADATA;

    static DatabaseMetadata databaseMetadata = ApplicationMetadata.EMPTY_DATABASE_METADATA;

    /**
     * 获取当前应用的应用上下文
     * <p>
     * 加载
     * {@code META-INF/services/com.harmony.umbrella.context.ApplicationContextProvider}
     * 文件中的实际类型来创建
     *
     * @return 应用上下文
     */
    public static final ApplicationContext getApplicationContext() {
        ApplicationContext context = null;
        ServiceLoader<ApplicationContextProvider> providers = ServiceLoader.load(ApplicationContextProvider.class);
        for (ApplicationContextProvider provider : providers) {
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
            context = SimpleApplicationContext.INSTANCE;
            LOG.debug("no context provider find, use default {}", SimpleApplicationContext.class.getName());
        }
        // 初始化
        context.init();
        return context;
    }

    public static void initStatic() {
    }

    /**
     * 设置当前线程的用户环境
     *
     * @param currentCtx
     *            用户环境
     */
    static void setCurrentContext(CurrentContext cc) {
        current.set(cc);
    }

    /**
     * 获取当前线程的用户环境
     *
     * @return 用户环境
     */
    public static CurrentContext getCurrentContext() {
        return current.get();
    }

    public static ServerMetadata getServerMetadata() {
        return serverMetadata;
    }

    public static DatabaseMetadata getDatabaseMetadata() {
        return databaseMetadata;
    }

    /**
     * 初始化应用上下文
     */
    public abstract void init();

    /**
     * 销毁应用上下文
     */
    public abstract void destroy();

    public abstract BeanFactory getBeanFactory();

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws BeansException {
        return getBeanFactory().getBean(beanClass, scope);
    }

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, String scope) throws BeansException {
        return getBeanFactory().getBean(beanName, scope);
    }

    private static final class SimpleApplicationContext extends ApplicationContext {

        private static final SimpleApplicationContext INSTANCE = new SimpleApplicationContext();

        private BeanFactory beanFactory = new SimpleBeanFactory();

        @Override
        public void init() {
        }

        @Override
        public void destroy() {
        }

        @Override
        public BeanFactory getBeanFactory() {
            return beanFactory;
        }

    }
}

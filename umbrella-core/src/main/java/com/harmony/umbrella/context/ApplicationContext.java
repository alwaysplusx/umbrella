package com.harmony.umbrella.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.NoSuchBeanFoundException;
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

    private static ServerMetadata serverMetadata = ApplicationMetadata.EMPTY_SERVER_METADATA;

    private static DatabaseMetadata databaseMetadata = ApplicationMetadata.EMPTY_DATABASE_METADATA;

    /**
     * 初始化应用上下文
     */
    public abstract void init();

    /**
     * 销毁应用上下文
     */
    public abstract void destroy();

    /**
     * 获取当前应用的应用上下文
     * <p>
     * 加载
     * {@code META-INF/services/com.harmony.umbrella.context.spi.ApplicationContextProvider}
     * 文件中的实际类型来创建
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
            LOG.debug("no context provider find, use default {}", SimpleApplicationContext.class.getName());
        }
        // 初始化
        context.init();
        return context;
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

    static void initialServerMetadata(ServletContext servletContext) {
        ApplicationContext.serverMetadata = ApplicationMetadata.getServerMetadata(servletContext);
    }

    static void initialDatabaseMetadata(Connection connection) throws SQLException {
        ApplicationContext.databaseMetadata = ApplicationMetadata.getDatabaseMetadata(connection);
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

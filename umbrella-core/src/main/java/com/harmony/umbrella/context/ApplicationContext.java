/*
 * Copyright 2013-2015 wuxii@foxmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.context;

import static com.harmony.umbrella.context.ApplicationMetadata.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ApplicationMetadata.DBInformation;
import com.harmony.umbrella.context.ApplicationMetadata.JVMInformation;
import com.harmony.umbrella.context.ApplicationMetadata.OSInformation;
import com.harmony.umbrella.context.ApplicationMetadata.ServerInformation;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.util.PropUtils;

/**
 * 运行的应用的上下文
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

    /**
     * jndi默认配置文件地址
     */
    public static final String APPLICATION_PROPERTIES_LOCATION = "META-INF/application.properties";

    protected static final ThreadLocal<CurrentContext> current = new InheritableThreadLocal<CurrentContext>();

    protected static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);

    private static final ServiceLoader<ContextProvider> providers = ServiceLoader.load(ContextProvider.class);

    /**
     * 应用的配置属性
     */
    protected final Properties applicationProperties = new Properties();

    protected Locale locale;

    /**
     * 运行的web服务信息，未初始化则为{@code null}
     */
    private static ServerInformation serverInfo;
    /**
     * 所连接的数据库信息, 未初始化则为{@code null}
     */
    private static DBInformation dbInfo;

    public ApplicationContext() {
    }

    public ApplicationContext(Properties props) {
        this.applicationProperties.putAll(props);
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
     * 设置当前线程的用户环境
     * 
     * @param currentCtx
     *            用户环境
     */
    public void setCurrentContext(CurrentContext currentCtx) {
        current.set(currentCtx);
    }

    /**
     * 获取当前线程的用户环境
     * 
     * @return 用户环境
     */
    public CurrentContext getCurrentContext() {
        return current.get();
    }

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
        return getApplicationContext0(new Properties());
    }

    /**
     * 获取当前应用的上下文, 并使用初始化属性{@code props}对应用进行初始化
     * 
     * @param props
     *            引用的初始化属性
     * @return 应用上下文
     */
    public static final ApplicationContext getApplicationContext(Properties props) {
        return getApplicationContext0(props);
    }

    /**
     * 获取当前应用的上下文, 并使用初始化属性{@code props}对应用进行初始化
     * 
     * @param props
     *            引用的初始化属性
     * @return 应用上下文
     */
    private static final ApplicationContext getApplicationContext0(Properties props) {

        Properties applicationProperties = new Properties();
        if (PropUtils.exists(APPLICATION_PROPERTIES_LOCATION)) {
            applicationProperties.putAll(props);
        }

        ApplicationContext context = null;
        synchronized (providers) {
            providers.reload();
            LOG.debug("current providers -> {}", providers);
            for (ContextProvider provider : providers) {
                try {
                    context = provider.createApplicationContext(applicationProperties);
                    if (context != null) {
                        LOG.debug("create context [{}] by [{}]", context, provider);
                        break;
                    }
                } catch (Exception e) {
                    LOG.warn("", e);
                }
            }
        }
        if (context == null) {
            LOG.warn("can't find any application context provider to create context, use default {}", ContextProvider.SimpleApplicationContext.class.getName());
            context = new ContextProvider.SimpleApplicationContext(applicationProperties);
        }
        return context;
    }

    public Properties getApplicationProperties() {
        return applicationProperties;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * 获取应用的jvm信息
     * 
     * @return jvm信息
     */
    public JVMInformation getInformationOfJVM() {
        return JVMINFO;
    }

    /**
     * 获取应用的服务信息
     * 
     * @return web服务器信息， 如果未初始化泽返回{@code null}
     */
    public ServerInformation getInformationOfServer() {
        return serverInfo;
    }

    /**
     * 获取应用的操作系统信息
     * 
     * @return 操作系统的信息
     */
    public OSInformation getInformationOfOS() {
        return OSINFO;
    }

    /**
     * 获取应用的数据库信息
     * 
     * @return 数据库信息, 未初始化返回{@code null}
     */
    public DBInformation getInformationOfDB() {
        return dbInfo;
    }

    /**
     * 注册应用的web服务信息
     * <p>
     * 一经注册就不在更改
     * 
     * @param servletContext
     *            web上下文
     */
    public void initializeServerInformation(ServletContext servletContext) {
        if (serverInfo == null) {
            serverInfo = METADATA.new ServerInformation(servletContext);
            LOG.info("init server information success\n{}", serverInfo);
        }
    }

    /**
     * 初始化应用的数据源信息
     * <p>
     * 一经初始化就不在更改
     * 
     * @param conn
     *            数据源的一个连接
     * @param close
     *            标识是否自动关闭数据源
     * @throws SQLException
     *             获取数据源信息失败
     */
    public void initializeDBInformation(Connection conn, boolean close) throws SQLException {
        if (dbInfo == null) {
            try {
                dbInfo = METADATA.new DBInformation(conn);
                LOG.info("init database information success\n{}", dbInfo);
            } finally {
                if (close) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }

    /**
     * 应用的概况
     */
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n")//
                .append("\"os\":").append(getInformationOfOS()).append(",\n")//
                .append("\"jvm\":").append(getInformationOfJVM()).append(",\n")//
                .append("\"db\":").append(getInformationOfDB() == null ? "{}" : getInformationOfDB()).append(",\n")//
                .append("\"server\":").append(getInformationOfServer() == null ? "{}" : getInformationOfServer()).append("\n")//
                .append("}");
        return sb.toString();
    }

}

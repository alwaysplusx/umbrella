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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import com.harmony.umbrella.context.ApplicationMetadata.DBInformation;
import com.harmony.umbrella.context.ApplicationMetadata.JVMInformation;
import com.harmony.umbrella.context.ApplicationMetadata.OSInformation;
import com.harmony.umbrella.context.ApplicationMetadata.ServerInformation;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.Environments;
import com.harmony.umbrella.util.PropUtils;

/**
 * 运行的应用的上下文
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

    protected static final Log LOG = Logs.getLog(ApplicationContext.class);
    
    public static final String APPLICATION_PACKAGE;

    /**
     * jndi默认配置文件地址
     */
    public static final String APPLICATION_PROPERTIES_LOCATION = "META-INF/application.properties";
    
    /**
     * 应用的配置属性
     */
    protected static final Properties applicationProperties = new Properties();

    static {
        APPLICATION_PACKAGE = Environments.getProperty("umbrella.application.package", "com.harmony");
        try {
            String fileLocation = Environments.getProperty("umbrella.application.properties.file", APPLICATION_PROPERTIES_LOCATION);
            applicationProperties.putAll(PropUtils.loadProperties(fileLocation));
        } catch (IOException e) {
            LOG.trace("META-INF/application.properties file not find, no default application properties");
        }
    }
    
    protected static final ThreadLocal<CurrentContext> current = new InheritableThreadLocal<CurrentContext>();


    private static final ServiceLoader<ContextProvider> providers = ServiceLoader.load(ContextProvider.class);

    protected Locale locale;

    /**
     * 运行的web服务信息，未初始化则为{@code null}
     */
    private static ServerInformation serverInfo;
    /**
     * 所连接的数据库信息, 未初始化则为{@code null}
     */
    private static DBInformation dbInfo;

    protected ApplicationContext() {
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
        return getApplicationContext0(null);
    }

    /**
     * 获取当前应用的上下文, 并使用初始化属性{@code props}对应用进行初始化
     *
     * @param url
     *            配置文件url
     * @return 应用上下文
     */
    public static final ApplicationContext getApplicationContext(URL url) {
        return getApplicationContext0(url);
    }

    /**
     * 获取当前应用的上下文, 并使用初始化属性{@code props}对应用进行初始化
     *
     * @param url
     *            配置文件url
     * @return 应用上下文
     */
    private static final ApplicationContext getApplicationContext0(URL url) {

        ApplicationContext context = null;
        synchronized (providers) {
            providers.reload();
            LOG.debug("current providers -> {}", providers);
            for (ContextProvider provider : providers) {
                try {
                    context = provider.createApplicationContext(url);
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
            LOG.warn("no context provider find, use default {}", ContextProvider.class.getName());
            context = ContextProvider.INSTANCE.createApplicationContext(url);
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

    public static String getProperty(String key) {
        return applicationProperties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return applicationProperties.getProperty(key, defaultValue);
    }

    public static void setProperty(String key, String value) {
        applicationProperties.put(key, value);
    }

}

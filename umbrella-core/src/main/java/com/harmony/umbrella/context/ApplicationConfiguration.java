package com.harmony.umbrella.context;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import com.harmony.umbrella.context.ApplicationContext.ApplicationContextInitializer;
import com.harmony.umbrella.core.ConnectionSource;
import com.harmony.umbrella.core.PropertyManager;

/**
 * 应用配置信息, 配置信息一旦创建就不允许更改
 *
 * @author wuxii@foxmail.com
 */
public interface ApplicationConfiguration {

    String getApplicationName();

    Set<String> getScanPackages();

    Class<? extends ApplicationContextInitializer> getApplicationContextInitializerClass();

    ServletContext getServletContext();

    List<ConnectionSource> getConnectionSources();

    Object getProperty(String key);

    String getStringProperty(String key);

    String getStringProperty(String key, String def);

    boolean getBooleanProperty(String key);

    boolean getBooleanProperty(String key, boolean def);

    Map<?, ?> getApplicationProperties();

    Class<? extends Runnable>[] getShutdownHooks();

    /**
     * read only property manager, 对property manager进行设置不影响其他配置
     * 
     * @return property manager
     */
    PropertyManager getPropertyManager();

}

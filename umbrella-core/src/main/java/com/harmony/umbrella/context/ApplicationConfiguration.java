package com.harmony.umbrella.context;

import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.core.PropertyManager;
import com.harmony.umbrella.core.SimplePropertyManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 应用配置信息, 配置信息一旦创建就不允许更改
 *
 * @author wuxii@foxmail.com
 */
public interface ApplicationConfiguration {

    String getApplicationName();

    ApplicationMetadata getApplicationMetadata();

    Map<Object, Object> getApplicationProperties();

    List<Class<? extends Runnable>> getShutdownHooks();

    default PropertyManager getPropertyManager() {
        return new SimplePropertyManager(Collections.unmodifiableMap(getApplicationProperties()));
    }

}

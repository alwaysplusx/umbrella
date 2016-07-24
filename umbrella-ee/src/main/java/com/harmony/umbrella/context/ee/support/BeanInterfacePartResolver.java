package com.harmony.umbrella.context.ee.support;

import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.context.ee.BeanDefinition;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class BeanInterfacePartResolver implements PartResolver<Class> {

    private ConfigManager configManager;

    public BeanInterfacePartResolver(ConfigManager configManager) {
        this.setConfigManager(configManager);
    }

    @Override
    public Set<Class> resolve(BeanDefinition bd) {
        Set<Class> result = new HashSet<Class>();
        if (bd.isRemoteClass()) {
            result.add(bd.getRemoteClass());
            return result;
        }
        result.addAll(bd.getAllRemoteClasses());
        return result;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}

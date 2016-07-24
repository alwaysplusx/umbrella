package com.harmony.umbrella.context.ee.support;

import static com.harmony.umbrella.context.ee.JndiConstanst.*;

import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.core.ClassWrapper;

/**
 * @author wuxii@foxmail.com
 */
public class BeanNamePartResolver implements PartResolver<String> {

    private ConfigManager configManager;

    public BeanNamePartResolver(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<String> resolve(BeanDefinition bd) {
        Set<String> result = new HashSet<String>();

        if (bd.isSessionClass()) {
            result.add(bd.getMappedName());
            return result;
        }

        Class<?> remoteClass = bd.getRemoteClass();

        ClassWrapper cw = new ClassWrapper(remoteClass);
        for (Class clazz : cw.getAllSubClasses()) {
            BeanDefinition subBd = new BeanDefinition(clazz);
            if (subBd.isSessionClass()) {
                result.add(subBd.getMappedName());
            }
        }

        final String remoteClassName = remoteClass.getSimpleName();

        Set<String> remoteSuffixSet = configManager.getPropertySet(JNDI_REMOTE);
        // add default
        remoteSuffixSet.add("Remote");
        remoteSuffixSet.remove("");

        String beanNameRemoveSuffix = remoteClassName;
        for (String remoteSuffix : remoteSuffixSet) {
            if (remoteClassName.endsWith(remoteSuffix)) {
                beanNameRemoveSuffix = remoteClassName.substring(0, remoteClassName.length() - remoteSuffix.length());
                break;
            }
        }

        Set<String> beanSuffixSet = configManager.getPropertySet(JNDI_BEAN);
        // add default
        beanSuffixSet.add("Bean");
        beanSuffixSet.add("");

        for (String beanSuffix : beanSuffixSet) {
            String beanName = beanNameRemoveSuffix + beanSuffix;
            if (!result.contains(beanName)) {
                result.add(beanName);
            }
        }

        return result;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

}

package com.harmony.umbrella.context.ee.support;

import java.util.Set;

import com.harmony.umbrella.context.ee.BeanDefinition;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigManager {

    //    protected final String globalPrefix;
    //    protected final Set<String> separators;
    //    protected final Set<String> beanSuffixes;
    //    protected final Set<String> remoteSuffixes;
    //    protected final Set<String> localSuffixes;
    //    protected final Set<WrappedBeanHandler> wrappedBeanHandlers;
    //    protected final boolean transformLocal;

    public Set<String> remoteNames(Class<?> remoteClass) {
        return null;
    }

    public Set<String> beanNames(BeanDefinition beanDefinition) {
        return null;
    }

    public Set<String> separators() {
        return null;
    }

    public String globalPrefix() {
        return null;
    }

    public Set<String> rootContext() {
        return null;
    }

}

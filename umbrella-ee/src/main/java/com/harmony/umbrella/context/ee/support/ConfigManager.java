package com.harmony.umbrella.context.ee.support;

import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigManager {

    protected final String globalPrefix;
    protected final Set<String> separators;
    protected final Set<String> beanSuffixes;
    protected final Set<String> remoteSuffixes;
    protected final Set<String> localSuffixes;
    protected final Set<WrappedBeanHandler> wrappedBeanHandlers;
    protected final boolean transformLocal;

}

package com.harmony.umbrella.context.ee.support;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanFilter;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.context.ee.ContextFactory;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;

/**
 * 通过配置配置的属性，来组合定义的bean
 *
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class ConfigurationBeanResolver implements BeanResolver {

    private static final Log log = Logs.getLog(ConfigurationBeanResolver.class);
    private ContextFactory contextFactory;

    public ConfigurationBeanResolver(ContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public ContextFactory getContextFactory() {
        return contextFactory;
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition) {
        return guessBean(beanDefinition, Collections.emptyMap());
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, Map properties) {
        return guessBean(beanDefinition, properties, null);
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, BeanFilter filter) {
        return guessBean(beanDefinition, Collections.emptyMap(), filter);
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, Map properties, BeanFilter filter) {
        Set<String> jndis = guessNames(beanDefinition, properties);
        for (String name : jndis) {
            Object bean = tryLookup(name);
            if (bean != null && isDeclareBean(beanDefinition, bean) && (filter == null || filter.accept(name, bean))) {
                return bean;
            }
        }
        return null;
    }

    public Set<String> guessNames(BeanDefinition beanDefinition, Map properties) {
        Set<String> jndis = new LinkedHashSet<String>();
        final String jndi = getValue(properties, "jndi", "lookup", "jndiname");
        if (jndi != null) {
            jndis.add(jndi);
        }
        jndis.addAll(c);
        return jndis;
    }

    /**
     * 通过jndi查找对应的bean
     *
     * @param jndi
     *            jndi
     * @param context
     *            javax.naming.Context
     * @return 如果未找到返回null
     */
    public Object tryLookup(String jndi) {
        try {
            return contextFactory.getContext().lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
    }

    @Override
    public boolean isDeclareBean(final BeanDefinition declare, final Object bean) {
        Class<?> remoteClass = declare.getRemoteClass();
        if (log.isDebugEnabled()) {
            log.debug("test, it is declare bean? "//
                    + "\n\tdeclare remoteClass -> {}" //
                    + "\n\tdeclare beanClass   -> {}" //
                    + "\n\tactual  bean        -> {}", //
                    remoteClass, declare.getBeanClass(), bean);
        }
        return declare.getBeanClass().isInstance(bean) || (remoteClass != null && remoteClass.isInstance(bean));
    }

    // 通过配置的wrappedBeanHandlers来解压实际的bean
    protected Object unwrap(Object bean) {
        return bean;
    }

    private String getValue(Map map, String... keys) {
        Object result = null;
        for (String key : keys) {
            result = map.get(key);
            if (result instanceof String && StringUtils.isNotBlank((String) result)) {
                return (String) result;
            }
        }
        return null;
    }

}

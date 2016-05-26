package com.harmony.umbrella.context.ee.support;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.harmony.umbrella.util.Converter;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ResolverFactory {

    static final Converter<String, Set<String>> stringToSetStringConverter = new Converter<String, Set<String>>() {
        @Override
        public Set<String> convert(String s) {
            Set<String> result = new HashSet<String>();
            if (StringUtils.isBlank(s)) {
                return result;
            }
            StringTokenizer st = new StringTokenizer(s, ",");
            while (st.hasMoreTokens()) {
                result.add(st.nextToken().trim());
            }
            return result;
        }
    };

    static final Converter<String, Boolean> stringToBooleanConverter = new Converter<String, Boolean>() {
        @Override
        public Boolean convert(String s) {
            if (StringUtils.isBlank(s)) {
                return false;
            }
            return Boolean.valueOf(s);
        }
    };

    static final Converter<String, Set<WrappedBeanHandler>> stringToSetWrappedBeanHandlerConverter = new Converter<String, Set<WrappedBeanHandler>>() {
        @Override
        public Set<WrappedBeanHandler> convert(String s) {
            Set<WrappedBeanHandler> result = new HashSet<WrappedBeanHandler>();
            if (StringUtils.isBlank(s)) {
                return result;
            }
            for (String className : stringToSetStringConverter.convert(s)) {
                try {
                    Class<?> clazz = Class.forName(className);
                    result.add((WrappedBeanHandler) ReflectionUtils.instantiateClass(clazz));
                } catch (ClassNotFoundException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
            }
            return result;
        }
    };

    // jndi.format.separator
    // jndi.format.bean
    // jndi.format.remote
    // jndi.format.local
    // jndi.format.transformLocal
    // jndi.wrapped.handler
    public static ConfigurationBeanResolver create(Properties properties) {
        String globalPrefix = properties.getProperty("jndi.format.global.prefix", "");
        if (!globalPrefix.endsWith("/")) {
            globalPrefix += "/";
        }
        return new ConfigurationBeanResolver(
                globalPrefix,
                getProperty(properties, "jndi.format.separator", "#", stringToSetStringConverter),
                getProperty(properties, "jndi.format.bean", "Bean, ", stringToSetStringConverter),
                getProperty(properties, "jndi.format.remote", "Remote, ", stringToSetStringConverter),
                getProperty(properties, "jndi.format.local", "Local, ", stringToSetStringConverter),
                getProperty(properties, "jndi.wrapped.handler", stringToSetWrappedBeanHandlerConverter),
                getProperty(properties, "jndi.format.transformLocal", "true", stringToBooleanConverter));
    }

    public static InternalContextResolver create(Properties properties) {
        String globalPrefix = properties.getProperty("jndi.format.global.prefix", "");
        if (!globalPrefix.endsWith("/")) {
            globalPrefix += "/";
        }
        return new InternalContextResolver(
                globalPrefix,
                getProperty(properties, "jndi.format.separator", "#", stringToSetStringConverter),
                getProperty(properties, "jndi.format.bean", "Bean, ", stringToSetStringConverter),
                getProperty(properties, "jndi.format.remote", "Remote, ", stringToSetStringConverter),
                getProperty(properties, "jndi.format.local", "Local, ", stringToSetStringConverter),
                getProperty(properties, "jndi.wrapped.handler", stringToSetWrappedBeanHandlerConverter),
                getProperty(properties, "jndi.format.transformLocal", "true", stringToBooleanConverter),
                getProperty(properties, "jndi.format.roots", "java:, ", stringToSetStringConverter),
                getProperty(properties, "jndi.format.maxDeeps", "10", stringToIntegerConverter)
        );
    }
    
    static <V> V getProperty(Properties properties, String key, Converter<String, V> converter) {
        return getProperty(properties, key, null, converter);
    }

    static <V> V getProperty(Properties properties, String key, String defaultValue, Converter<String, V> converter) {
        return converter.convert(properties.getProperty(key, defaultValue));
    }

    static final Converter<String, ? extends Integer> stringToIntegerConverter = new Converter<String, Integer>() {
        @Override
        public Integer convert(String s) {
            if (StringUtils.isBlank(s)) {
                return 0;
            }
            return Integer.valueOf(s);
        }
    };
}

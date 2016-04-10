/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.context.ee.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanFilter;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.context.ee.WrappedBeanHandler;
import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.core.ClassWrapper;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.Converter;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 通过配置配置的属性，来组合定义的bean
 *
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class ConfigurationBeanResolver implements BeanResolver {

    private static final Log log = Logs.getLog(ConfigurationBeanResolver.class);

    protected final String globalPrefix;
    protected final Set<String> separators;
    protected final Set<String> beanSuffixes;
    protected final Set<String> remoteSuffixes;
    protected final Set<String> localSuffixes;
    protected final Set<WrappedBeanHandler> wrappedBeanHandlers;
    protected final boolean transformLocal;

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

    static <V> V getProperty(Properties properties, String key, Converter<String, V> converter) {
        return getProperty(properties, key, null, converter);
    }

    static <V> V getProperty(Properties properties, String key, String defaultValue, Converter<String, V> converter) {
        return converter.convert(properties.getProperty(key, defaultValue));
    }

    public ConfigurationBeanResolver(String globalPrefix,
                                     Set<String> separators,
                                     Set<String> beanSuffixes,
                                     Set<String> remoteSuffixes,
                                     Set<String> localSuffixes,
                                     Set<WrappedBeanHandler> wrappedBeanHandlers,
                                     boolean transformLocale) {
        this.globalPrefix = globalPrefix;
        this.separators = separators;
        this.beanSuffixes = beanSuffixes;
        this.remoteSuffixes = remoteSuffixes;
        this.localSuffixes = localSuffixes;
        this.wrappedBeanHandlers = wrappedBeanHandlers;
        this.transformLocal = transformLocale;
    }

    @Override
    public String[] guessNames(BeanDefinition beanDefinition) {
        return guessNames0(beanDefinition, null, null);
    }

    @Override
    public String[] guessNames(BeanDefinition beanDefinition, Context context) {
        Assert.notNull(context, "context not allow null");
        return guessNames0(beanDefinition, null, context);
    }

    @Override
    public String[] guessNames(BeanDefinition beanDefinition, EJB ejbAnnotation) {
        return guessNames0(beanDefinition, ejbAnnotation, null);
    }

    @Override
    public String[] guessNames(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context) {
        Assert.notNull(context);
        return guessNames0(beanDefinition, ejbAnnotation, context);
    }

    private String[] guessNames0(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context) {
        if (beanDefinition.isRemoteClass()) {
            return new RemoteClassJndiGuesser(beanDefinition, ejbAnnotation, context).guess();
        } else if (beanDefinition.isLocalClass()) {
            return new LocalClassJndiGuesser(beanDefinition, ejbAnnotation, context).guess();
        } else if (beanDefinition.isSessionClass()) {
            return new SessionClassJndiGuesser(beanDefinition, ejbAnnotation, context).guess();
        }
        throw new BeansException("unsupported bean definition");
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, Context context) {
        Assert.notNull(context);
        return guessBean0(beanDefinition, null, context, null);
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, Context context, BeanFilter filter) {
        Assert.notNull(filter, "filter is not allow null");
        Assert.notNull(context);
        return guessBean0(beanDefinition, null, context, filter);
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context) {
        Assert.notNull(ejbAnnotation, "ejbAnnotation is not allow null");
        Assert.notNull(context);
        return guessBean0(beanDefinition, ejbAnnotation, context, null);
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context, BeanFilter filter) {
        Assert.notNull(context);
        Assert.notNull(filter);
        Assert.notNull(ejbAnnotation);
        return guessBean0(beanDefinition, ejbAnnotation, context, filter);
    }

    protected Object guessBean0(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context, BeanFilter filter) {
        Object bean = null;
        String[] jndiNames = guessNames0(beanDefinition, ejbAnnotation, context);
        for (String jndi : jndiNames) {
            bean = tryLookup(jndi, context);
            if (bean != null && (filter == null || filter.accept(jndi, bean))) {
                break;
            }
        }
        return bean;
    }

    @Override
    public boolean isDeclareBean(BeanDefinition declare, Object bean) {
        return isDeclare(declare, unwrap(bean));
    }

    /**
     * 测试目标bean是否是声明的类型
     */
    protected boolean isDeclare(BeanDefinition declare, Object bean) {
        Class<?> remoteClass = declare.getRemoteClass();
        if (log.isDebugEnabled()) {
            log.debug("test, it is declare bean? "
                            + "\n\tdeclare remoteClass -> {}"
                            + "\n\tdeclare beanClass   -> {}"
                            + "\n\tactual  bean        -> {}",
                    remoteClass, declare.getBeanClass(), bean);
        }
        return declare.getBeanClass().isInstance(bean) || (remoteClass != null && remoteClass.isInstance(bean));
    }

    // 通过配置的wrappedBeanHandlers来解压实际的bean
    protected Object unwrap(Object bean) {
        for (WrappedBeanHandler handler : wrappedBeanHandlers) {
            if (handler.isWrappedBean(bean)) {
                return handler.unwrap(bean);
            }
        }
        return bean;
    }

    /**
     * 通过jndi查找对应的bean
     *
     * @param jndi
     *         jndi
     * @param context
     *         javax.naming.Context
     * @return 如果未找到返回null
     */
    public Object tryLookup(String jndi, Context context) {
        try {
            return context.lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
    }

    // ### jndi guesser
    @SuppressWarnings("unchecked")
    private abstract class JndiGuesser {

        final Context context;
        final BeanDefinition beanDefinition;
        final ClassWrapper classWrapper;
        final LinkedHashSet<String> jndiSet = new LinkedHashSet<String>();
        final EJB ejbAnnotation;
        final boolean transformLocal;

        public JndiGuesser(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context) {
            this.context = context;
            this.beanDefinition = beanDefinition;
            this.classWrapper = new ClassWrapper(beanDefinition.getBeanClass());
            this.ejbAnnotation = ejbAnnotation;
            this.transformLocal = ConfigurationBeanResolver.this.transformLocal;
        }

        protected abstract Collection<String> mappedNames();

        protected abstract Collection<Class> remoteClasses();

        /**
         * 通过配置信息猜想可能的jndi
         */
        public final String[] guess() {
            if (ejbAnnotation != null && StringUtils.isNotBlank(ejbAnnotation.lookup())) {
                // 如果直接配置了ejbAnnotation的lookup = 直接配置了jndi. 不再猜想直接返回配置项
                this.jndiSet.add(ejbAnnotation.lookup());
            } else {
                for (String mappedName : mappedNames()) {
                    for (String separator : separators) {
                        for (Class remoteClass : remoteClasses()) {
                            addIfAbsent(mappedName, separator, remoteClass);
                        }
                    }
                }
            }
            return jndiSet.toArray(new String[jndiSet.size()]);
        }

        /**
         * 格式划jndi, 再判断jndi是否存在于上下文中
         * <p/>
         * <p/>
         * <pre>
         *   JNDI = prefix() + beanSuffix + separator + package + . + prefix() + remoteSuffix
         * </pre>
         *
         * @param mappedName
         *         bean的映射名称
         * @param separator
         *         分割符
         * @param remoteClass
         *         remote的类型
         */
        protected void addIfAbsent(String mappedName, String separator, Class<?> remoteClass) {
            String jndi = new StringBuilder(globalPrefix).append(mappedName).append(separator).append(remoteClass.getName()).toString();
            if (!jndiSet.contains(jndi) && (context == null || tryLookup(jndi, context) != null)) {
                jndiSet.add(jndi);
            }
        }

        /**
         * 当前beanDefinition所有子类的mappedName, 如果beanDefinition是session class则没有子mappedName
         */
        protected Set<String> subMappedNames() {
            Set<String> mappedNames = new HashSet<String>();
            if (beanDefinition.isSessionClass()) {
                // 如果自身就是session bean返回的值为空
                return mappedNames;
            }
            // 事实上存在的mappedName
            for (Class subClass : classWrapper.getAllSubClasses()) {
                BeanDefinition sbd = new BeanDefinition(subClass);
                if (sbd.isSessionClass()) {
                    // 存在session bean的注解
                    mappedNames.add(sbd.getMappedName());
                }
            }
            return mappedNames;
        }

        /**
         * 如果有传入@EJB注解,通过注解上的mappedName为字段注入的mappedName
         */
        public String fieldMappedName() {
            return ejbAnnotation != null ? (String) AnnotationUtils.getAnnotationValue(ejbAnnotation, "mappedName") : null;
        }

        /**
         * 通过接口的类名+配置bean后缀信息, 猜想对应的mappedName
         *
         * @param classes
         *         接口类
         * @param suffixes
         *         bean的后缀
         * @return
         */
        public Set<String> guessMappedNames(Collection<Class> classes, Collection<String> suffixes) {
            Set<String> mappedNames = new HashSet<String>(classes.size());
            for (Class<?> clazz : classes) {
                final String name = clazz.getSimpleName();
                for (String suffix : suffixes) {
                    // 去除后缀, 如果没有对应的后缀则忽略
                    String mappedName = name;
                    if (mappedName.endsWith(suffix)) {
                        mappedName = mappedName.substring(0, mappedName.lastIndexOf(suffix));
                    }
                    for (String beanSuffix : beanSuffixes) {
                        // 给去除后缀后的localClass SimpleName增加上bean的名称后缀
                        mappedNames.add(mappedName + beanSuffix);
                    }
                }
            }
            return mappedNames;
        }

        /**
         * 将local class的类通过local suffix + remote suffix替换方式来得出remoteClass
         */
        protected final Set<Class> transformLocalToRemote() {
            Set<Class> result = new HashSet<Class>();
            for (Class<?> localClass : beanDefinition.getAllLocalClasses()) {
                final String localClassName = localClass.getName();
                for (String localSuffix : localSuffixes) {
                    String name = localClassName;
                    if (name.endsWith(localSuffix)) {
                        name = name.substring(0, name.lastIndexOf(localSuffix));
                    }
                    for (String remoteSuffix : remoteSuffixes) {
                        try {
                            result.add(Class.forName(name + remoteSuffix, false, ClassUtils.getDefaultClassLoader()));
                        } catch (Throwable e) {
                        }
                    }
                }
            }
            return result;
        }
    }

    final class RemoteClassJndiGuesser extends JndiGuesser {


        public RemoteClassJndiGuesser(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context) {
            super(beanDefinition, ejbAnnotation, context);
        }

        /**
         * 通过remote接口猜想mappedName
         * <p/>
         * <ul> <li>首先匹配remoteClass的所有子类,通过对子类的判断来获取mappedName</li> <li>如果有@EJB注解, 通过注解上的名称mappedName来获取对于的名称</li>
         * <li>通过配置的猜想,通过替换远程接口的后缀来猜想mappedName</li> </ul>
         */
        protected Collection<String> mappedNames() {
            Set<String> mappedNames = subMappedNames();
            mappedNames.addAll(guessMappedNames(beanDefinition.getAllRemoteClasses(), remoteSuffixes));
            String fieldMappedName = fieldMappedName();
            if (StringUtils.isNotBlank(fieldMappedName)) {
                mappedNames.add(fieldMappedName);
            }
            return mappedNames;
        }

        protected Collection<Class> remoteClasses() {
            return beanDefinition.getAllRemoteClasses();
        }

    }

    final class LocalClassJndiGuesser extends JndiGuesser {

        public LocalClassJndiGuesser(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context) {
            super(beanDefinition, ejbAnnotation, context);
        }

        /**
         * 通过local class猜想mappedName
         * <p/>
         * <ul> <li>首先匹配localClass的所有子类,通过对子类的判断来获取mappedName</li> <li>如果有@EJB注解, 通过注解上的名称mappedName来获取对于的名称</li>
         * <li>通过配置的猜想,通过替换local接口的后缀来猜想mappedName</li> </ul>
         */
        @Override
        protected Collection<String> mappedNames() {
            Set<String> mappedNames = new HashSet<String>(subMappedNames());
            mappedNames.addAll(guessMappedNames(beanDefinition.getAllLocalClasses(), localSuffixes));
            if (transformLocal) {
                mappedNames.addAll(guessMappedNames(remoteClasses(), remoteSuffixes));
            }
            return mappedNames;
        }

        @Override
        protected Collection<Class> remoteClasses() {
            return transformLocal ? transformLocalToRemote() : new ArrayList<Class>();
        }
    }

    final class SessionClassJndiGuesser extends JndiGuesser {

        public SessionClassJndiGuesser(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context) {
            super(beanDefinition, ejbAnnotation, context);
        }

        @Override
        protected Collection<String> mappedNames() {
            return Arrays.asList(beanDefinition.getMappedName());
        }

        @Override
        protected Collection<Class> remoteClasses() {
            Set<Class> remoteClasses = new HashSet<Class>(beanDefinition.getAllRemoteClasses());
            if (transformLocal) {
                remoteClasses.addAll(transformLocalToRemote());
            }
            return remoteClasses;
        }
    }
}

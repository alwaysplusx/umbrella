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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ApplicationContextException;
import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanFilter;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.context.ee.WrappedBeanHandler;
import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.core.ClassWrapper;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 通过配置配置的属性，来组合定义的bean
 *
 * @author wuxii@foxmail.com
 */
public class ConfigurationBeanResolver implements BeanResolver {

    private static final Log log = Logs.getLog(ConfigurationBeanResolver.class);

    private final Properties containerProperties = new Properties();

    /**
     * jndi的全局前缀
     */
    protected final String globalPrefix;

    /**
     * jndi名称与remoteClass中间的分割符, default '#'
     */
    protected final Set<String> separators;

    /**
     * 组装mappedName需要添加的后缀
     */
    protected final Set<String> beanSuffixes;

    /**
     * jndi需要添加的class后缀
     */
    protected final Set<String> remoteSuffixes;

    /**
     * local的后缀
     */
    protected final Set<String> localSuffixes;

    /**
     * lookup到的bean如果是对应于应用服务器的封装类的解析工具
     */
    protected final List<WrappedBeanHandler> wrappedBeanHandlers = new ArrayList<WrappedBeanHandler>();

    /**
     * 开启local接口转化
     */
    private boolean transformLocal;

    public ConfigurationBeanResolver(Properties props) {
        this.containerProperties.putAll(props);

        this.globalPrefix = getProperty("jndi.format.global.prefix", "");
        this.separators = getFromProperties("jndi.format.separator", "#");
        this.beanSuffixes = getFromProperties("jndi.format.bean", "Bean, ");
        this.remoteSuffixes = getFromProperties("jndi.format.remote", "Remote, ");
        this.localSuffixes = getFromProperties("jndi.format.local", "Local, ");
        this.transformLocal = Boolean.valueOf(getProperty("jndi.format.transformLocal", "true"));
        this.init();
    }

    private void init() throws ApplicationContextException {
        Set<String> classNames = getFromProperties("jndi.wrapped.handler");
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                this.wrappedBeanHandlers.add((WrappedBeanHandler) ReflectionUtils.instantiateClass(clazz));
            }
        } catch (Throwable e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }
    }

    public String getProperty(String key) {
        return containerProperties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return containerProperties.getProperty(key, defaultValue);
    }

    public Set<String> getFromProperties(String key) {
        return getFromProperties(key, null);
    }

    public Set<String> getFromProperties(String key, String defaultValue) {
        String property = getProperty(key, defaultValue);
        if (StringUtils.isBlank(property)) {
            return Collections.emptySet();
        }
        StringTokenizer st = new StringTokenizer(property, ",");
        Set<String> result = new HashSet<String>(st.countTokens());
        while (st.hasMoreTokens()) {
            result.add(st.nextToken().trim());
        }
        return result;
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, Context context, BeanFilter filter) {
        Assert.notNull(filter, "bean filter not allow null");
        Object bean = null;
        String[] guessJndis = guessNames(beanDefinition, context);
        for (String jndi : guessJndis) {
            bean = tryLookup(jndi, context);
            if (bean != null && filter.accept(jndi, bean)) {
                break;
            }
        }
        return bean;
    }

    @Override
    public String[] guessNames(BeanDefinition beanDefinition) {
        return guessNames0(beanDefinition, null);
    }

    /*
     * method despatch
     * 
     * @see com.harmony.umbrella.context.ee.BeanResolver#guessNames(BeanDefinition)
     */
    @Override
    public String[] guessNames(BeanDefinition beanDefinition, Context context) {
        Assert.notNull(context, "context not allow null");
        return guessNames0(beanDefinition, context);
    }

    private String[] guessNames0(BeanDefinition beanDefinition, Context context) {
        if (beanDefinition.isSessionClass()) {
            // 为session bean
            return new SessionResolver(beanDefinition, context).resolve();
        } else if (beanDefinition.isRemoteClass()) {
            // 为remote接口, 主要功能
            return new RemoteResolver(beanDefinition, context).resolve();
        } else if (beanDefinition.isLocalClass()) {
            // 为local接口
            return new LocalResolver(beanDefinition, context).resolve();
        }
        throw new BeansException("unsupport bean definition");
    }

    @Override
    public boolean isDeclareBean(BeanDefinition declare, Object bean) {
        return isDeclare(declare, unwrap(bean));
    }

    protected boolean isDeclare(BeanDefinition declare, Object bean) {
        Class<?> remoteClass = declare.getSuitableRemoteClass();
        if (log.isDebugEnabled()) {
            log.debug("\ntest it is declare bean? "//
                    + "\n\tremoteClass -> {}"//
                    + "\n\tbeanClass   -> {}"//
                    + "\n\tbean        -> {}",//
                    remoteClass, declare.getBeanClass(), bean);
        }
        // FIXME remoteClass isInstance
        return declare.getBeanClass().isInstance(bean) || (remoteClass != null && remoteClass.isInstance(bean));
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
    public Object tryLookup(String jndi, Context context) {
        try {
            return context.lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
    }

    // 判断jndi是否在context中存在
    private boolean existsInContext(String jndi, Context context) {
        return tryLookup(jndi, context) != null;
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

    public String getGlobalPrefix() {
        return globalPrefix;
    }

    public void setTransformLocal(boolean transformLocal) {
        this.transformLocal = transformLocal;
    }

    public static final String removeSuffix(String target, String suffix) {
        if (target.endsWith(suffix)) {
            int index = target.lastIndexOf(suffix);
            return target.substring(0, index);
        }
        return target;
    }

    /**
     * 通过bean定义以及{@linkplain ConfigurationBeanResolver}中的配置属性来猜想对应的jndi名称
     *
     * @author wuxii@foxmail.com
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public abstract class ConcreteBeanResolver {

        protected final Context context;
        protected final BeanDefinition beanDefinition;
        protected final ClassWrapper classWrapper;
        protected final Set<String> jndis = new HashSet<String>();

        public ConcreteBeanResolver(BeanDefinition beanDefinition, Context context) {
            this.beanDefinition = beanDefinition;
            this.context = context;
            this.classWrapper = new ClassWrapper(beanDefinition.getBeanClass());
        }

        protected abstract Collection<String> mappedNames();

        protected abstract Collection<Class> remoteClasses();

        public final String[] resolve() {
            for (String mappedName : mappedNames()) {
                for (String separator : separators) {
                    for (Class remoteClass : remoteClasses()) {
                        addJndiIfAbsent(mappedName, separator, remoteClass);
                    }
                }
            }
            return jndis.toArray(new String[jndis.size()]);
        }

        /*protected void addJndiIfAbsent(String mappedName, Class<?> remoteClass) {
            for (String separator : separators) {
                StringBuilder sb = new StringBuilder();
                sb.append(globalPrefix);
                // FIXME 全局环境添加 /
                if (!globalPrefix.endsWith("/")) {
                    sb.append("/");
                }
                sb.append(mappedName).append(separator).append(remoteClass.getName());
                String jndi = sb.toString();
                if (!jndis.contains(jndi) && (context == null || existsInContext(jndi, context))) {
                    jndis.add(jndi);
                }
            }
        }*/

        /**
         * 格式划jndi, 再判断jndi是否存在于上下文中
         * <p>
         * <p>
         * 
         * <pre>
         *   JNDI = prefix() + beanSuffix + separator + package + . + prefix() + remoteSuffix
         * </pre>
         *
         * @param mappedName
         *            bean的映射名称
         * @param separator
         *            分割符
         * @param remoteClass
         *            remote的类型
         */
        protected void addJndiIfAbsent(String mappedName, String separator, Class<?> remoteClass) {
            String jndi = new StringBuilder(globalPrefix).append(mappedName).append(separator).append(remoteClass.getName()).toString();
            if (!jndis.contains(jndi) && (context == null || existsInContext(jndi, context))) {
                jndis.add(jndi);
            }
        }

        /**
         * 当前beanDefinition所有子类的mappedName, 如果beanDefinition是session
         * class则没有子mappedName
         */
        protected Set<String> suberMappedNames() {
            if (beanDefinition.isSessionClass()) {
                // 如果自身就是session bean返回的值为空
                return Collections.emptySet();
            }
            Set<String> mappedNames = new HashSet<String>();
            // 事实上存在的mappedName
            for (Class subClass : classWrapper.getAllSuberClasses()) {
                BeanDefinition suberBeanDefinition = new BeanDefinition(subClass);
                if (suberBeanDefinition.isSessionClass()) {
                    // 存在session bean的注解
                    mappedNames.add(suberBeanDefinition.getMappedName());
                }
            }
            return mappedNames;
        }

        /**
         * 通过接口的类名+配置bean后缀信息, 猜想对应的mappedName
         * 
         * @param classes
         *            接口类
         * @param suffixes
         *            bean的后缀
         * @return
         */
        public Set<String> guessMappedNames(Collection<Class> classes, Collection<String> suffixes) {
            Set<String> mappedNames = new HashSet<String>(classes.size());
            for (Class<?> clazz : classes) {
                String name = clazz.getSimpleName();
                for (String suffix : suffixes) {
                    // 去除后缀, 如果没有对应的后缀则忽略
                    name = removeSuffix(name, suffix);
                    for (String beanSuffix : beanSuffixes) {
                        // 给去除后缀后的localClass SimpleName增加上bean的名称后缀
                        mappedNames.add(name + beanSuffix);
                    }
                }
            }
            return mappedNames;
        }

        /**
         * 将local class的类通过local suffix + remote suffix替换方式来得出remoteClass
         */
        protected final Set<Class> transofrmLocalToRemote() {
            Set<Class> result = new HashSet<Class>();
            for (Class<?> localClass : beanDefinition.getLocalClasses()) {
                String name = localClass.getName();
                for (String localSuffix : localSuffixes) {
                    name = removeSuffix(name, localSuffix);
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

    /**
     * 通过remote接口查找会话bean的解决策略
     * <p>
     * <p>
     * <p>
     * 
     * <pre>
     * 如: com.harmony.FooRemote
     * 
     *      remoteSuffixs = Remote
     *      beanSuffixs = Bean
     *      beanSeparators = #
     * 
     * 结果为：
     * 
     *      FooBean#com.harmony.FooRemote
     * </pre>
     */
    @SuppressWarnings("rawtypes")
    final class RemoteResolver extends ConcreteBeanResolver {

        public RemoteResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
        }

        protected Collection<String> mappedNames() {
            Set<String> mappedNames = suberMappedNames();
            mappedNames.addAll(guessMappedNames(beanDefinition.getRemoteClasses(), remoteSuffixes));
            return mappedNames;
        }

        protected Collection<Class> remoteClasses() {
            return Arrays.<Class> asList(beanDefinition.getBeanClass());
        }

    }

    /**
     * 通过local接口查找会话bean解决策略
     * <p>
     * <p>
     * <p>
     * 
     * <pre>
     *  如：com.harmony.FooLocal
     *      beanSuffix = Bean
     *      localSuffix = Local
     *      remoteSuffix = Remote
     * 
     *  结果为 ：
     * 
     *      FooBean#com.harmony.FooLocal
     * 
     *  另，可开启local转化关系{@linkplain ConfigurationBeanResolver#transformLocal transformLocal}为true， 将local的结尾转为remote形式
     * 
     *  结果为:
     *      FooBean#com.harmony.FooRemote
     * </pre>
     *
     * @author wuxii@foxmail.com
     */
    @SuppressWarnings("rawtypes")
    final class LocalResolver extends ConcreteBeanResolver {

        private final boolean transformLocal;

        public LocalResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
            this.transformLocal = ConfigurationBeanResolver.this.transformLocal;
        }

        /*
         * 去除localClass上的后缀, 添加上beanSuffix组合成一个mappedName
         */
        @Override
        protected Collection<String> mappedNames() {
            Set<String> mappedNames = suberMappedNames();
            mappedNames.addAll(guessMappedNames(beanDefinition.getLocalClasses(), localSuffixes));
            if (transformLocal) {
                mappedNames.addAll(guessMappedNames(remoteClasses(), remoteSuffixes));
            }
            return mappedNames;
        }

        protected Collection<Class> remoteClasses() {
            if (this.transformLocal) {
                return transofrmLocalToRemote();
            }
            return beanDefinition.getLocalClasses();
        }
    }

    /**
     * 通过会话bean找到会话beans实例
     * <p>
     * <p>
     * <p>
     * 
     * <pre>
     *  如：com.harmony.FooBean
     * 
     *      remoteSuffix = Remote
     *      beanSuffix = Bean
     *  结果为：
     * 
     *     FooBean#com.harmony.FooRemote
     *
     * </pre>
     *
     * @author wuxii@foxmail.com
     */
    @SuppressWarnings("rawtypes")
    final class SessionResolver extends ConcreteBeanResolver {

        private final boolean transformLocal;

        public SessionResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
            this.transformLocal = ConfigurationBeanResolver.this.transformLocal;
        }

        @Override
        protected Collection<String> mappedNames() {
            return Arrays.asList(beanDefinition.getMappedName());
        }

        @Override
        protected Collection<Class> remoteClasses() {
            Set<Class> remoteClasses = new HashSet<Class>(beanDefinition.getRemoteClasses());
            if (this.transformLocal) {
                remoteClasses.addAll(transofrmLocalToRemote());
            }
            return remoteClasses;
        }
    }

}

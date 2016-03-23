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
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanFilter;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.context.ee.WrappedBeanHandler;
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
    protected final List<String> separators = new ArrayList<String>();

    /**
     * 组装mappedName需要添加的后缀
     */
    protected final List<String> beanSuffixes = new ArrayList<String>();

    /**
     * jndi需要添加的class后缀
     */
    protected final List<String> remoteSuffixes = new ArrayList<String>();

    /**
     * local的后缀
     */
    protected final List<String> localSuffixes = new ArrayList<String>();

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
        this.globalPrefix = props.getProperty("jndi.format.global.prefix", "");
        init();
    }

    private void init() {
        final Properties p = containerProperties;

        this.separators.addAll(splitProperty(p.getProperty("jndi.format.separator", "#")));
        this.beanSuffixes.addAll(splitProperty(p.getProperty("jndi.format.bean", "Bean, ")));
        this.remoteSuffixes.addAll(splitProperty(p.getProperty("jndi.format.remote", "Remote, ")));
        this.localSuffixes.addAll(splitProperty(p.getProperty("jndi.format.local", "Local, ")));
        this.transformLocal = Boolean.valueOf(p.getProperty("jndi.format.transformLocal", "true"));

        String property = p.getProperty("jndi.wrapped.handler");
        if (property != null) {
            Set<String> classNames = splitProperty(property);
            for (String className : classNames) {
                try {
                    Class<?> clazz = Class.forName(className);
                    this.wrappedBeanHandlers.add((WrappedBeanHandler) ReflectionUtils.instantiateClass(clazz));
                } catch (Throwable e) {
                    log.warn("{}", e);
                }
            }
        }
    }

    /**
     * 读取资源文件的内容， 并将内容分割问set对象
     */
    protected Set<String> splitProperty(String property) {
        StringTokenizer st = new StringTokenizer(property, ",");
        Set<String> result = new HashSet<String>(st.countTokens());
        while (st.hasMoreTokens()) {
            result.add(st.nextToken().trim());
        }
        return result;
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
        return declare.getBeanClass().isInstance(bean) || (remoteClass != null && remoteClass.isInstance(bean));
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, Context context, BeanFilter filter) {
        Assert.notNull(filter);
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
        Assert.notNull(context);
        return guessNames0(beanDefinition, context);
    }

    private String[] guessNames0(BeanDefinition beanDefinition, Context context) {
        if (beanDefinition.isSessionBean()) {
            return new SessionResolver(beanDefinition, context).resolve();

        } else if (beanDefinition.isRemoteClass()) {
            return new RemoteResolver(beanDefinition, context).resolve();

        } else if (beanDefinition.isLocalClass()) {
            return new LocalResolver(beanDefinition, context).resolve();

        }
        throw new RuntimeException("unsupport bean definition");
    }

    private boolean existsInContext(String jndi, Context context) {
        return tryLookup(jndi, context) != null;
    }

    protected Object unwrap(Object bean) {
        for (WrappedBeanHandler handler : wrappedBeanHandlers) {
            if (handler.isWrappedBean(bean)) {
                return handler.unwrap(bean);
            }
        }
        return bean;
    }

    public Object tryLookup(String jndi, Context context) {
        try {
            return context.lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
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

    @SuppressWarnings("rawtypes")
    public abstract class ConcreteBeanResolver {

        protected final Context context;
        protected final BeanDefinition beanDefinition;
        protected final Set<String> jndis = new HashSet<String>();

        public ConcreteBeanResolver(BeanDefinition beanDefinition, Context context) {
            this.beanDefinition = beanDefinition;
            this.context = context;
        }

        protected abstract Collection<Class> remoteClasses();

        protected abstract Collection<String> mappedNames();

        public final String[] resolve() {
            for (String mappedName : mappedNames()) {
                for (Class remoteClass : remoteClasses()) {
                    addJndiIfAbsent(mappedName, remoteClass);
                }
            }
            return jndis.toArray(new String[jndis.size()]);
        }

        /**
         * 格式划jndi, 再判断jndi是否存在于上下文中
         * <p/>
         * 
         * <pre>
         *   JNDI = prefix() + beanSuffix + separator + package + . + prefix() + remoteSuffix
         * </pre>
         *
         * @param mappedName
         *            bean的映射名称
         * @param remoteClass
         *            remote的类型
         */
        protected void addJndiIfAbsent(String mappedName, Class<?> remoteClass) {
            for (String separator : separators) {
                StringBuilder sb = new StringBuilder();
                sb.append(globalPrefix);
                if (!globalPrefix.endsWith("/")) {
                    sb.append("/");
                }
                sb.append(mappedName).append(separator).append(remoteClass.getName());

                String jndi = sb.toString();

                if (!jndis.contains(jndi) && (context == null || existsInContext(jndi, context))) {
                    jndis.add(jndi);
                }
            }
        }

        /**
         * 将local class的类通过local suffix + remote suffix替换方式来得出localClass
         */
        protected final Set<Class> transofrmLocal() {
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

        public Set<String> transformMappedName(List<Class> classes, List<String> suffixes) {
            Set<String> result = new HashSet<String>(classes.size());
            for (Class<?> clazz : classes) {
                String name = clazz.getSimpleName();
                for (String suffix : suffixes) {
                    // 去除后缀, 如果没有对应的后缀则忽略
                    name = removeSuffix(name, suffix);
                    for (String beanSuffix : beanSuffixes) {
                        // 给去除后缀后的localClass SimpleName增加上bean的名称后缀
                        result.add(name + beanSuffix);
                    }
                }
            }
            return result;
        }
    }

    /**
     * 通过local接口查找会话bean解决策略
     * <p/>
     * <p/>
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
            String mappedName = beanDefinition.getMappedName();
            if (StringUtils.isNotBlank(mappedName)) {
                return Arrays.asList(mappedName);
            } else {
                return transformMappedName(beanDefinition.getLocalClasses(), localSuffixes);
            }
        }

        protected Collection<Class> remoteClasses() {
            if (this.transformLocal) {
                return transofrmLocal();
            }
            return beanDefinition.getLocalClasses();
        }
    }

    /**
     * 通过remote接口查找会话bean的解决策略
     * <p/>
     * <p/>
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
            String mappedName = beanDefinition.getMappedName();
            if (StringUtils.isNotBlank(mappedName)) {
                return Arrays.asList(mappedName);
            } else {
                return transformMappedName(beanDefinition.getRemoteClasses(), remoteSuffixes);
            }
        }

        protected Collection<Class> remoteClasses() {
            return beanDefinition.getRemoteClasses();
        }

    }

    /**
     * 通过会话bean找到会话beans实例
     * <p/>
     * <p/>
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
            String mappedName = beanDefinition.getMappedName();
            if (StringUtils.isBlank(mappedName)) {
                mappedName = beanDefinition.getBeanClass().getSimpleName();
            }
            return Arrays.asList(mappedName);
        }

        @Override
        protected Collection<Class> remoteClasses() {
            Set<Class> remoteClasses = new HashSet<Class>(beanDefinition.getRemoteClasses());
            if (this.transformLocal) {
                remoteClasses.addAll(transofrmLocal());
            }
            return remoteClasses;
        }
    }

}

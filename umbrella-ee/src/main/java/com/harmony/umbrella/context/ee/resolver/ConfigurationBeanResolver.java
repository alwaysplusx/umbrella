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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.context.ee.WrappedBeanHandler;
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

    private static final Logger log = LoggerFactory.getLogger(ConfigurationBeanResolver.class);

    /**
     * jndi的全局前缀
     */
    protected final String globalPrefix;

    /**
     * jndi名称与remoteClass中间的分割符, default '#'
     */
    protected final Set<String> separators = new HashSet<String>();

    /**
     * 组装mappedName需要添加的后缀
     */
    protected final Set<String> beanSuffixs = new HashSet<String>();

    /**
     * jndi需要添加的class后缀
     */
    protected final Set<String> remoteSuffixs = new HashSet<String>();

    /**
     * local对于的后缀
     */
    protected final Set<String> localSuffixs = new HashSet<String>();

    /**
     * lookup到的bean如果是对应于应用服务器的封装类的解析工具
     */
    protected final List<WrappedBeanHandler> warppedBeanHandlers = new ArrayList<WrappedBeanHandler>();

    /**
     * 开启local接口转化
     */
    private boolean transformLocal;

    public ConfigurationBeanResolver(Properties props) {
        this.globalPrefix = props.getProperty("jndi.format.globlal.prefix", "");
        this.transformLocal = Boolean.valueOf(props.getProperty("jndi.format.transformLocal"));
        this.beanSuffixs.addAll(fromProps(props, "jndi.format.bean"));
        this.separators.addAll(fromProps(props, "jndi.format.separator"));
        this.remoteSuffixs.addAll(fromProps(props, "jndi.format.remote"));
        this.localSuffixs.addAll(fromProps(props, "jndi.format.local"));
        this.warppedBeanHandlers.addAll(createFromProps(props, "jndi.wrapped.handler", WrappedBeanHandler.class));
    }

    /**
     * 读取资源文件中的值， 根据值对于的内容创建需要的实例
     */
    @SuppressWarnings("unchecked")
    protected <T> Set<T> createFromProps(Properties props, String key, Class<T> requireType) {
        String value = props.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return Collections.emptySet();
        }
        StringTokenizer st = new StringTokenizer(value, ",");
        Set<T> result = new HashSet<T>(st.countTokens());
        while (st.hasMoreTokens()) {
            String className = st.nextToken().trim();
            Class<T> clazz;
            try {
                clazz = (Class<T>) Class.forName(className, false, ClassUtils.getDefaultClassLoader());
                result.add(ReflectionUtils.instantiateClass(clazz));
            } catch (Exception e) {
                log.warn("", e);
            }
        }
        return result;
    }

    /**
     * 读取资源文件的内容， 并将内容分割问set对象
     */
    protected Set<String> fromProps(Properties props, String key) {
        String value = props.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return Collections.emptySet();
        }
        StringTokenizer st = new StringTokenizer(value, ",");
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
            log.debug("test is declare bean? "//
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
        for (String jndi : guessNames(beanDefinition)) {
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
        for (WrappedBeanHandler handler : warppedBeanHandlers) {
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

    public List<WrappedBeanHandler> getWrappedBeanHandlers() {
        return warppedBeanHandlers;
    }

    public Set<String> getBeanSuffixs() {
        return beanSuffixs;
    }

    public Set<String> getRemoteSuffixs() {
        return remoteSuffixs;
    }

    public Set<String> getLocalSuffixs() {
        return localSuffixs;
    }

    public String getGlobalPrefix() {
        return globalPrefix;
    }

    public void setTransformLocal(boolean transformLocal) {
        this.transformLocal = transformLocal;
    }

    public static final String removeSuffix(String target, String suffix) {
        int index = target.lastIndexOf(suffix);
        return index > 0 ? target.substring(0, index) : target;
    }

    public abstract class ConcreteBeanResolver {

        protected final BeanDefinition beanDefinition;
        protected final Context context;
        protected final Set<String> jndis = new HashSet<String>();

        public ConcreteBeanResolver(BeanDefinition beanDefinition, Context context) {
            this.beanDefinition = beanDefinition;
            this.context = context;
        }

        public String[] resolve() {

            for (String mappedName : mappedNames()) {
                for (Class<?> remoteClass : remoteClasses()) {
                    addIfNotExists(mappedName, remoteClass);
                }
            }

            return jndis.toArray(new String[jndis.size()]);
        }

        protected abstract Set<Class<?>> remoteClasses();

        protected abstract Set<String> mappedNames();

        /**
         * 格式划jndi, 再判断jndi是否存在于上下文中
         * 
         * <pre>
         *   JNDI = prefix() + beanSuffix + separator + package + . + prefix() + remoteSuffix
         * </pre>
         * 
         * @param beanSuffix
         *            映射名的后缀
         * @param remoteSuffix
         *            结尾的后缀
         */
        protected void addIfNotExists(String mappedName, Class<?> remoteClass) {
            for (String separator : separators) {
                StringBuilder sb = new StringBuilder();
                sb.append(globalPrefix);
                if (StringUtils.isNotBlank(globalPrefix) //
                        && !globalPrefix.endsWith("/") //
                        && !mappedName.startsWith("/")) {
                    sb.append("/");
                }
                sb.append(mappedName)//
                        .append(separator)//
                        .append(remoteClass.getName());
                String jndi = sb.toString();

                if (!jndis.contains(jndi) && (context == null || existsInContext(jndi, context))) {
                    jndis.add(jndi);
                }
            }
        }
    }

    /**
     * 通过local接口查找会话bean解决策略
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
        protected Set<String> mappedNames() {
            Set<String> result = new LinkedHashSet<String>();

            String mappedName = beanDefinition.getMappedName();

            if (StringUtils.isNotBlank(mappedName)) {
                result.add(mappedName);
            } else {
                Class<?>[] localClasses = beanDefinition.getLocalClasses();

                for (Class<?> localClass : localClasses) {
                    String name = localClass.getSimpleName();
                    for (String localSuffix : localSuffixs) {
                        // 去除后缀, 如果没有对应的后缀则忽略
                        name = removeSuffix(name, localSuffix);
                        for (String beanSuffix : beanSuffixs) {
                            // 给去除后缀后的localClass SimpleName增加上bean的名称后缀
                            result.add(name + beanSuffix);
                        }
                    }
                }
            }
            return result;
        }

        protected Set<Class<?>> remoteClasses() {
            Class<?>[] localClasses = beanDefinition.getLocalClasses();
            Set<Class<?>> result = new LinkedHashSet<Class<?>>(localClasses.length);

            if (!this.transformLocal) {

                Collections.addAll(result, localClasses);

            } else {

                for (Class<?> localClass : localClasses) {
                    String name = localClass.getName();
                    for (String localSuffix : localSuffixs) {
                        if (name.endsWith(localSuffix)) {
                            name = removeSuffix(name, localSuffix);
                            for (String remoteSuffix : remoteSuffixs) {
                                try {
                                    result.add(Class.forName(name + remoteSuffix, false, ClassUtils.getDefaultClassLoader()));
                                } catch (ClassNotFoundException e) {
                                }
                            }
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
     * 
     */
    final class RemoteResolver extends ConcreteBeanResolver {

        public RemoteResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
        }

        protected Set<String> mappedNames() {
            Set<String> result = new LinkedHashSet<String>();
            String mappedName = beanDefinition.getMappedName();

            if (StringUtils.isNotBlank(mappedName)) {
                result.add(mappedName);
            } else {

                Class<?>[] remoteClasses = beanDefinition.getRemoteClasses();

                for (Class<?> remoteClass : remoteClasses) {
                    String name = remoteClass.getSimpleName();

                    for (String remoteSuffix : remoteSuffixs) {
                        if (name.endsWith(remoteSuffix)) {
                            name = removeSuffix(name, remoteSuffix);
                            for (String beanSuffix : beanSuffixs) {
                                result.add(name + beanSuffix);
                            }
                        }
                    }

                }

            }
            return result;
        }

        protected Set<Class<?>> remoteClasses() {
            Class<?>[] remoteClasses = beanDefinition.getRemoteClasses();
            Set<Class<?>> result = new LinkedHashSet<Class<?>>(remoteClasses.length);
            Collections.addAll(result, remoteClasses);
            return result;
        }

    }

    /**
     * 通过会话bean找到会话beans实例
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
    final class SessionResolver extends ConcreteBeanResolver {

        private final boolean transformLocal;

        public SessionResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
            this.transformLocal = ConfigurationBeanResolver.this.transformLocal;
        }

        @Override
        protected Set<String> mappedNames() {

            Set<String> result = new LinkedHashSet<String>();
            String mappedName = beanDefinition.getMappedName();

            if (StringUtils.isNotBlank(mappedName)) {
                result.add(mappedName);
            } else {
                result.add(beanDefinition.getBeanClass().getSimpleName());
            }

            return result;
        }

        @Override
        protected Set<Class<?>> remoteClasses() {
            Class<?>[] remoteClasses = beanDefinition.getRemoteClasses();
            Set<Class<?>> result = new LinkedHashSet<Class<?>>();
            Collections.addAll(result, remoteClasses);
            if (this.transformLocal) {
                Class<?>[] localClasses = beanDefinition.getLocalClasses();
                for (Class<?> localClass : localClasses) {
                    String name = localClass.getName();
                    for (String localSuffix : localSuffixs) {
                        if (name.endsWith(localSuffix)) {
                            name = removeSuffix(name, localSuffix);
                            for (String remoteSuffix : remoteSuffixs) {
                                try {
                                    result.add(Class.forName(name + remoteSuffix, false, ClassUtils.getDefaultClassLoader()));
                                } catch (ClassNotFoundException e) {
                                }
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

}

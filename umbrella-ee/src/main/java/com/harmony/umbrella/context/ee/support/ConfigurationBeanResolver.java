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
package com.harmony.umbrella.context.ee.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
 * @author wuxii@foxmail.com
 */
public class ConfigurationBeanResolver implements BeanResolver {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationBeanResolver.class);

    /**
     * mappedName与之后的连接符号
     */
    private final Set<String> separators = new HashSet<String>(Arrays.asList("#"));

    /**
     * 组装mappedName需要添加的后缀
     */
    private final Set<String> beanSuffixs = new HashSet<String>();

    /**
     * jndi需要添加的class后缀
     */
    private final Set<String> remoteSuffixs = new HashSet<String>();

    /**
     * local对于的后缀
     */
    private final Set<String> localSuffixs = new HashSet<String>();

    /**
     * jndi的全局前缀
     */
    private final String globalPrefix;

    /**
     * lookup到的bean如果是对应于应用服务器的封装类的解析工具
     */
    private final List<WrappedBeanHandler> warppedBeanHandlers = new ArrayList<WrappedBeanHandler>();

    /**
     * 开启local接口转化
     */
    private boolean transformLocal;

    public ConfigurationBeanResolver(Properties props) {
        this.globalPrefix = props.getProperty("jndi.format.globlal.prefix", "");
        this.transformLocal = Boolean.valueOf(props.getProperty("jndi.format.transformLocal"));
        this.beanSuffixs.addAll(fromProps(props, "jndi.format.bean"));
        this.remoteSuffixs.addAll(fromProps(props, "jndi.format.remote"));
        this.localSuffixs.addAll(fromProps(props, "jndi.format.local"));
        this.separators.addAll(fromProps(props, "jndi.format.separator"));
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

    public Set<String> getBeanSeparators() {
        return separators;
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

    private static final Set<String> blankSet = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("")));

    public abstract class ConcreteBeanResolver {

        protected final BeanDefinition beanDefinition;
        protected final Context context;
        protected final Set<String> jndis = new HashSet<String>();

        private String removedSuffix;
        private String prefix;
        private String _package;

        public ConcreteBeanResolver(BeanDefinition beanDefinition, Context context) {
            this.beanDefinition = beanDefinition;
            this.context = context;
        }

        public abstract String[] resolve();

        protected abstract Set<String> getRemovedSuffixs();

        /**
         * beanDefinition的bean的package
         */
        public String getPackage() {
            if (_package == null) {
                this._package = beanDefinition.getBeanClass().getPackage().getName();
            }
            return _package;
        }

        /**
         * 对beanDefinition的类名通过{@linkplain #getRemovedSuffixs()}
         * 比对如果类名是以后缀结尾则移除对应的结尾，并记录移除的结尾。
         * 
         * @return 去除对应后缀的bean名称
         */
        public String prefix() {
            if (prefix == null) {
                prefix = hasMappedName() ? beanDefinition.getMappedName() : beanDefinition.getBeanClass().getSimpleName();
                for (String suffix : getRemovedSuffixs()) {
                    if (prefix.endsWith(suffix)) {
                        removedSuffix = suffix;
                        prefix = removeSuffix(prefix, suffix);
                        break;
                    }
                }
            }
            return prefix;
        }

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
        protected void addIfExists(String beanSuffix, String remoteSuffix) {
            for (String separator : separators) {
                String jndi = toJndi(beanSuffix, separator, remoteSuffix);
                if (!jndis.contains(jndi) && (context == null || existsInContext(jndi, context))) {
                    jndis.add(jndi);
                }
            }
        }

        private String toJndi(String bean, String separator, String remote) {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix()).append(bean)//
                    .append(separator).append(getPackage()).append(".")//
                    .append(prefix()).append(remote);
            return sb.toString();
        }

        /**
         * 被移除的后缀
         */
        protected String getRemovedSuffix() {
            if (removedSuffix == null) {
                prefix();
                if (removedSuffix == null) {
                    removedSuffix = "";
                }
            }
            return removedSuffix;
        }

        /**
         * beanDefinition是否存在原始的映射名称
         */
        protected boolean hasMappedName() {
            return StringUtils.isNotBlank(beanDefinition.getMappedName());
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

        @Override
        protected Set<String> getRemovedSuffixs() {
            return localSuffixs;
        }

        @Override
        public String[] resolve() {

            Set<String> bss = hasMappedName() ? blankSet : beanSuffixs;
            Set<String> rss = !this.transformLocal ? new HashSet<String>(Arrays.asList(getRemovedSuffix())) : remoteSuffixs;

            for (String bs : bss) {
                for (String rs : rss) {
                    addIfExists(bs, rs);
                }
            }

            return jndis.toArray(new String[jndis.size()]);
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

        @Override
        public String[] resolve() {
            Set<String> bss = hasMappedName() ? blankSet : beanSuffixs;

            for (String bs : bss) {
                addIfExists(bs, getRemovedSuffix());
            }

            return jndis.toArray(new String[jndis.size()]);
        }

        @Override
        protected Set<String> getRemovedSuffixs() {
            return remoteSuffixs;
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

        public SessionResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
        }

        @Override
        public String[] resolve() {

            for (String rs : remoteSuffixs) {
                addIfExists(getRemovedSuffix(), rs);
            }

            return jndis.toArray(new String[jndis.size()]);
        }

        @Override
        protected Set<String> getRemovedSuffixs() {
            return beanSuffixs;
        }

    }

}

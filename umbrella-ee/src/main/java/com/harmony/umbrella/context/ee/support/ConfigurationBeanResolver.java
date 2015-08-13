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
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.context.ee.WrappedBeanHandler;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurationBeanResolver implements BeanResolver {

    private final Set<String> beanSeparators = new HashSet<String>(Arrays.asList("#"));

    private final Set<String> beanSuffixs = new HashSet<String>();

    private final Set<String> remoteSuffixs = new HashSet<String>();

    private final Set<String> localSuffixs = new HashSet<String>();

    private final Set<String> globalPrefix = new HashSet<String>();

    private final List<WrappedBeanHandler> warppedBeanHandlers = new ArrayList<WrappedBeanHandler>();

    private boolean transformLocal;

    public ConfigurationBeanResolver() {
    }

    @Override
    public boolean isDeclareBean(BeanDefinition declaer, Object bean) {
        for (WrappedBeanHandler handler : warppedBeanHandlers) {
            if (handler.isWrappedBean(bean)) {
                bean = handler.unwrap(bean);
                break;
            }
        }
        Class<?> remoteClass = declaer.getSuitableRemoteClass();
        return declaer.getBeanClass().isInstance(bean) || (remoteClass != null && remoteClass.isInstance(bean));
    }

    @Override
    public String[] guessNames(BeanDefinition beanDefinition) {
        if (beanDefinition.isSessionBean()) {
            return new SessionResolver(beanDefinition, null).resolve();

        } else if (beanDefinition.isRemoteClass()) {
            return new RemoteResolver(beanDefinition, null).resolve();

        } else if (beanDefinition.isLocalClass()) {
            return new LocalResolver(beanDefinition, null).resolve();

        }
        throw new RuntimeException("unsupport bean definition");
    }

    public Object guessBean(BeanDefinition beanDefinition, Context context, SessionBeanAccept filter) {
        return null;
    }

    /*
     * method despatch
     * 
     * @see com.harmony.umbrella.context.ee.BeanResolver#guessNames(BeanDefinition)
     */
    @Override
    public String[] guessNames(BeanDefinition beanDefinition, Context context) {
        Assert.notNull(context, "context is not alow null");
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
        return beanSeparators;
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

    public Set<String> getGlobalPrefix() {
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

    /**
     * @author wuxii@foxmail.com
     */
    public interface SessionBeanAccept {

        boolean accept(String jndi, Object bean);

    }

    public abstract class ConcreteBeanResolver {

        private final BeanDefinition beanDefinition;
        private final Context context;
        final Set<String> jndis = new HashSet<String>();

        public ConcreteBeanResolver(BeanDefinition beanDefinition, Context context) {
            this.beanDefinition = beanDefinition;
            this.context = context;
        }

        public abstract String mappedPrefix();

        public abstract String lastPrefix();

        public abstract String[] resolve();

        public Context getContext() {
            return context;
        }

        public BeanDefinition getBeanDefinition() {
            return beanDefinition;
        }

        protected void addIfExists(String prefix, String separator, String suffix) {
            String jndi = String.format("%s%s%s", prefix, separator, suffix);
            if (!jndis.contains(jndi) && (context == null || existsInContext(jndi, context))) {
                jndis.add(jndi);
            }
        }

        protected String getMappedName(String suffix) {
            Assert.notNull(suffix);
            return mappedPrefix() + suffix;
        }

        protected String getSuffix(String suffix) {
            Assert.notNull(suffix);
            return lastPrefix() + suffix;
        }

        protected boolean hasMappedName() {
            return StringUtils.isNotBlank(beanDefinition.getMappedName());
        }

        protected String getOriginalMappedName() {
            return beanDefinition.getMappedName();
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
    private class LocalResolver extends ConcreteBeanResolver {

        private final boolean transformLocal;
        private String mappedNamePrefix;
        private String lastPrefix;

        public LocalResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
            this.transformLocal = ConfigurationBeanResolver.this.transformLocal;
        }

        @Override
        public String mappedPrefix() {
            if (mappedNamePrefix == null) {
                if (hasMappedName()) {
                    mappedNamePrefix = getOriginalMappedName();
                } else {
                    mappedNamePrefix = getBeanDefinition().getBeanClass().getSimpleName();
                    for (String suffix : localSuffixs) {
                        if (mappedNamePrefix.endsWith(suffix)) {
                            mappedNamePrefix = removeSuffix(mappedNamePrefix, suffix);
                            break;
                        }
                    }
                }
            }
            return mappedNamePrefix;
        }

        @Override
        public String lastPrefix() {
            if (lastPrefix == null) {
                lastPrefix = getBeanDefinition().getBeanClass().getName();
                if (this.transformLocal) {
                    for (String suffix : localSuffixs) {
                        if (lastPrefix.endsWith(suffix)) {
                            lastPrefix = removeSuffix(lastPrefix, suffix);
                            break;
                        }
                    }
                }
            }
            return lastPrefix;
        }

        @Override
        public String[] resolve() {

            Set<String> bss = hasMappedName() ? blankSet : beanSuffixs;
            Set<String> rss = !this.transformLocal ? blankSet : remoteSuffixs;

            for (String separator : beanSeparators) {
                for (String bs : bss) {
                    String mappedName = getMappedName(bs);
                    for (String rs : rss) {
                        addIfExists(mappedName, separator, getSuffix(rs));
                    }
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
    private class RemoteResolver extends ConcreteBeanResolver {

        private String mappedNamePrefix;
        private String lastPrefix;

        public RemoteResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
        }

        @Override
        public String[] resolve() {
            Set<String> bss = hasMappedName() ? blankSet : beanSuffixs;

            for (String separator : beanSeparators) {
                for (String bs : bss) {
                    addIfExists(getMappedName(bs), separator, lastPrefix());
                }
            }

            return jndis.toArray(new String[jndis.size()]);
        }

        @Override
        public String mappedPrefix() {
            if (mappedNamePrefix == null) {
                if (hasMappedName()) {
                    mappedNamePrefix = getOriginalMappedName();
                } else {
                    mappedNamePrefix = getBeanDefinition().getBeanClass().getSimpleName();
                    for (String suffix : remoteSuffixs) {
                        if (mappedNamePrefix.endsWith(suffix)) {
                            mappedNamePrefix = removeSuffix(mappedNamePrefix, suffix);
                            break;
                        }
                    }
                }
            }
            return mappedNamePrefix;
        }

        @Override
        public String lastPrefix() {
            if (lastPrefix == null) {
                lastPrefix = getBeanDefinition().getBeanClass().getName();
            }
            return lastPrefix;
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
    private class SessionResolver extends ConcreteBeanResolver {

        private String mappedNamePrefix;
        private String lastPrefix;

        public SessionResolver(BeanDefinition beanDefinition, Context context) {
            super(beanDefinition, context);
        }

        @Override
        public String[] resolve() {

            for (String separator : beanSeparators) {
                for (String rs : remoteSuffixs) {
                    addIfExists(mappedPrefix(), separator, getSuffix(rs));
                }
            }
            return jndis.toArray(new String[jndis.size()]);
        }

        @Override
        public String mappedPrefix() {
            if (mappedNamePrefix == null) {
                if (hasMappedName()) {
                    mappedNamePrefix = getOriginalMappedName();
                } else {
                    mappedNamePrefix = getBeanDefinition().getBeanClass().getSimpleName();
                }
            }
            return mappedNamePrefix;
        }

        @Override
        public String lastPrefix() {
            if (lastPrefix == null) {
                lastPrefix = getBeanDefinition().getBeanClass().getName();
                for (String bs : beanSuffixs) {
                    if (lastPrefix.endsWith(bs)) {
                        lastPrefix = removeSuffix(lastPrefix, bs);
                        break;
                    }
                }
            }
            return lastPrefix;
        }
    }

}

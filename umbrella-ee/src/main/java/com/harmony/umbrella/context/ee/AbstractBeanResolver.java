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
package com.harmony.umbrella.context.ee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Context;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractBeanResolver implements BeanResolver {

    private final Set<String> beanSeparators = new HashSet<String>(Arrays.asList("#"));

    private final Set<String> beanSuffixs = new HashSet<String>();

    private final Set<String> remoteSuffixs = new HashSet<String>();

    private final Set<String> localSuffixs = new HashSet<String>();

    private final Set<String> globalPrefix = new HashSet<String>();

    public AbstractBeanResolver() {
    }

    @Override
    public boolean isDeclareBean(BeanDefinition declaer, Object bean) {
        return false;
    }

    /*
     * method despatch
     * 
     * @see com.harmony.umbrella.context.ee.BeanResolver#guessNames(BeanDefinition)
     */
    @Override
    public String[] guessNames(BeanDefinition beanDefinition, Context context) {
        if (beanDefinition.isSessionBean()) {
            return new SessionResolver(beanDefinition, context).resolve();

        } else if (beanDefinition.isRemoteClass()) {
            return new RemoteResolver(beanDefinition, context).resolve();

        } else if (beanDefinition.isLocalClass()) {
            return new LocalResolver(beanDefinition, context).resolve();

        }
        throw new RuntimeException("unsupport bean definition");
    }

    /* *//**
     * 解析localClass的jndi
     * 
     * @param mappedName
     *            bean definition中的mappedName
     * @param localClass
     *            注解为@Local的class
     * @param beanDefinition
     *            源信息
     * @return beanDefinition对应的jndi
     */
    /*
    protected String[] guessNameOfLocal(String mappedName, Class<?> localClass, BeanDefinition beanDefinition, Context context) {
     Set<String> jndis = new HashSet<String>();

     if (StringUtils.isNotBlank(mappedName)) {
         addIfExists(mappedName, localClass, jndis, context);
     }

     String localName = localClass.getSimpleName();

     for (String localSuffix : localSuffixs) {
         for (String beanSuffix : beanSuffixs) {
             mappedName = buildMappedName(localName, localSuffix, beanSuffix);
             addIfExists(mappedName, localClass, jndis, context);
         }
     }

     return jndis.toArray(new String[jndis.size()]);
    }

    protected String[] guessNameOfRemote(String mappedName, Class<?> remoteClass, BeanDefinition beanDefinition, Context context) {
     Set<String> jndis = new HashSet<String>();

     if (StringUtils.isNotBlank(mappedName)) {
         addIfExists(mappedName, remoteClass, jndis, context);
     }

     for (String remoteSuffix : remoteSuffixs) {
         for (String beanSuffix : beanSuffixs) {
             mappedName = buildMappedName(remoteClass, remoteSuffix, beanSuffix);
             addIfExists(mappedName, remoteClass, jndis, context);
         }
     }

     return jndis.toArray(new String[jndis.size()]);
    }

    protected String[] guessNameOfBean(String mappedName, Class<?> beanClass, BeanDefinition beanDefinition, Context context) {
     return null;
    }

    private String buildMappedName(String name, String suffix, String append) {
     int index = name.lastIndexOf(suffix);
     if (index > 0) {
         return String.format("%s%s", name.substring(0, index), append);
     }
     return name;
    }

    private void addIfExists(String prefix, Class<?> clazz, Set<String> jndis, Context context) {
     for (String separator : beanSeparators) {
         String jndi = String.format("%s%s%s", prefix, separator, suffix);
         if (existsInContext(jndi, context)) {
             jndis.add(jndi);
         }
     }
    }

    private boolean existsInContext(String jndi, Context context) {
     return tryLookup(jndi, context) != null;
    }

    protected Object tryLookup(String jndi, Context context) {
     try {
         // return context.lookup(jndi);
         return 1;
     } catch (Exception e) {
         return null;
     }
    }*/

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

    public abstract class ConcreteBeanResolver {

        public abstract String[] resolve();

    }

    /**
     * 将remoteClass的名称按{@linkplain #remoteSuffixs}去除后缀而后添加上
     * {@linkplain #beanSuffixs}的bean名称的后缀再加上 {@linkplain #beanSeparators}中的分割符
     * + remoteClass的全类名作为JavaEE环境中的映射JNDI， 并将计算后的JNDI在上下文中尝试lookup，
     * 有获取到对象则为一个可以映射的jndi名称
     * <p>
     * 
     * <pre>
     * 如: com.harmony.FooRemote 作为remoteClass
     * 
     *      remoteSuffixs = Remote
     *      beanSuffixs = Bean
     *      beanSeparators = #
     *      
     * 则计算后的jndi名称可为
     * 
     *      FooBean#com.harmony.FooRemote
     * </pre>
     * 
     */
    @SuppressWarnings("unused")
    private class RemoteResolver extends ConcreteBeanResolver {

        private final BeanDefinition beanDefinition;
        private final Context context;

        public RemoteResolver(BeanDefinition beanDefinition, Context context) {
            this.beanDefinition = beanDefinition;
            this.context = context;
        }

        @Override
        public String[] resolve() {
            return null;
        }

    }

    @SuppressWarnings("unused")
    private class LocalResolver extends ConcreteBeanResolver {

        private final BeanDefinition beanDefinition;
        private final Context context;

        public LocalResolver(BeanDefinition beanDefinition, Context context) {
            this.beanDefinition = beanDefinition;
            this.context = context;
        }

        @Override
        public String[] resolve() {
            return null;
        }

    }

    @SuppressWarnings("unused")
    private class SessionResolver extends ConcreteBeanResolver {

        private final BeanDefinition beanDefinition;
        private final Context context;

        public SessionResolver(BeanDefinition beanDefinition, Context context) {
            this.beanDefinition = beanDefinition;
            this.context = context;
        }

        @Override
        public String[] resolve() {
            return null;
        }

    }

}

/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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

import javax.naming.Context;

/**
 * @author wuxii@foxmail.com
 */
public interface BeanResolver {

    /**
     * 根据配置的上下文的信息猜想环境中对应的bean jndi名称
     * 
     * @param beanDefinition
     *            bean定义
     * @return 所有猜想并在{@linkplain Context}中存在的jndi名称
     */
    String[] guessNames(BeanDefinition beanDefinition, Context root);

    /**
     * 通过配置的信息获取可猜想出的jndi
     * 
     * @param beanDefinition
     *            bean定义
     * @return jndis
     */
    String[] guessNames(BeanDefinition beanDefinition);

    /**
     * 查看bean是否与声明的类型匹配
     * 
     * @param declaer
     *            声明的bean定义
     * @param bean
     *            待检验的bean
     * @return 符合定义的bean返回true
     */
    boolean isDeclareBean(BeanDefinition declare, Object bean);

    /**
     * @param beanDefinition
     * @param context
     * @param filter
     * @return
     */
    Object guessBean(BeanDefinition beanDefinition, Context context, BeanFilter filter);

    /**
     * @author wuxii@foxmail.com
     */
    public interface BeanFilter {

        /**
         * 判断jndi对应的bean是否为所需要的bean
         * 
         * @param jndi
         *            jndi名称
         * @param bean
         *            bean实例
         */
        boolean accept(String jndi, Object bean);

    }
}

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
package com.harmony.umbrella.ee;

import javax.naming.Context;

/**
 * 在bean解析的基础上添加在context中查找对应bean的功能
 * 
 * @author wuxii@foxmail.com
 */
public interface ContextResolver extends BeanResolver {

    /**
     * 在context中查找beanDefinition对于的SessionBean
     * 
     * @param beanDefinition
     *            bean定义
     * @param context
     *            context
     * @return 与beanDefinition相对应的SessionBean， 没有找到返回null
     */
    SessionBean search(BeanDefinition beanDefinition, Context context);

    /**
     * 在context中lookup jndi对象
     * 
     * @param jndi
     *            环境中的jndi
     * @param context
     *            context
     * @return 如果context中没有对应的jndi返回null
     */
    Object tryLookup(String jndi, Context context);

    /**
     * 清除查找到的结果缓存
     */
    void clear();

}

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
package com.harmony.umbrella.context.ee.resolver;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.SessionBean;

/**
 * @author wuxii@foxmail.com
 */
public final class SimpleSessionBean implements SessionBean {

    final BeanDefinition beanDefinition;
    Object bean;
    String jndi;
    boolean wrapped;

    protected SimpleSessionBean(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    protected SimpleSessionBean(BeanDefinition beanDefinition, String jndi, Object bean, boolean wrapped) {
        this.beanDefinition = beanDefinition;
        this.bean = bean;
        this.jndi = jndi;
        this.wrapped = wrapped;
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getJndi() {
        return jndi;
    }

    @Override
    public boolean isCacheable() {
        return beanDefinition.isStateless();
    }

    @Override
    public boolean isWrapped() {
        return wrapped;
    }

    @Override
    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beanDefinition == null) ? 0 : beanDefinition.hashCode());
        result = prime * result + ((bean == null) ? 0 : bean.hashCode());
        result = prime * result + ((jndi == null) ? 0 : jndi.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleSessionBean other = (SimpleSessionBean) obj;
        if (beanDefinition == null) {
            if (other.beanDefinition != null)
                return false;
        } else if (!beanDefinition.equals(other.beanDefinition))
            return false;
        if (bean == null) {
            if (other.bean != null)
                return false;
        } else if (!bean.equals(other.bean))
            return false;
        if (jndi == null) {
            if (other.jndi != null)
                return false;
        } else if (!jndi.equals(other.jndi))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append(" : {beanDefinition:");
        builder.append(beanDefinition);
        builder.append(", jndi:");
        builder.append(jndi);
        builder.append(", bean:");
        builder.append(bean);
        builder.append("}");
        return builder.toString();
    }

}
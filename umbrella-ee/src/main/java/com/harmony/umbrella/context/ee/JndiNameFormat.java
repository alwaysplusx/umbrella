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

import com.harmony.umbrella.context.NameFormat;

/**
 * // TODO 根据jndiFormat生成jndiName // final String jndiFormat = //
 * jndiProperties.getProperty("jee.jndi.format");
 * 
 * @author wuxii@foxmail.com
 */
class JndiNameFormat implements NameFormat {

    @Override
    public String format(Class<?> clazz) {
        return null;
    }

    @Override
    public String format(String beanName) {
        // String beanName = null;
        // final String beanNameSuffix =
        // jndiProperties.getProperty("jee.jndi.beanName.suffix", "Bean");
        // if (clazz.isInterface()) {
        // final String interfaceClassSuffix =
        // jndiProperties.getProperty("jee.jndi.interfaceClass.suffix",
        // "Remote");
        // String interfaceClassName = clazz.getSimpleName();
        // int suffixIndex =
        // interfaceClassName.lastIndexOf(interfaceClassSuffix);
        // if (suffixIndex != -1) {
        // beanName = interfaceClassName.substring(0, suffixIndex) +
        // beanNameSuffix;
        // } else {
        // beanName = interfaceClassName + beanNameSuffix;
        // }
        // } else {
        // Annotation ann = clazz.getAnnotation(Singleton.class);
        // if (ann != null) {
        // beanName = "".equals(((Singleton) ann).mappedName()) ?
        // clazz.getSimpleName() : ((Singleton) ann).mappedName();
        // }
        // ann = clazz.getAnnotation(Stateful.class);
        // if (ann != null) {
        // beanName = "".equals(((Stateful) ann).mappedName()) ?
        // clazz.getSimpleName() : ((Stateful) ann).mappedName();
        // }
        // ann = clazz.getAnnotation(Stateless.class);
        // if (ann != null) {
        // beanName = "".equals(((Stateless) ann).mappedName()) ?
        // clazz.getSimpleName() : ((Stateless) ann).mappedName();
        // }
        // if (beanName == null) {
        // beanName = clazz.getSimpleName();
        // }
        // }
        // return beanName;
        return null;
    }

}

/*
 * Copyright 2012-2016 the original author or authors.
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
package com.harmony.umbrella.context.ee.util;

import java.util.Arrays;
import java.util.List;

import com.harmony.umbrella.context.ee.WrappedBeanHandler;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
public class WeblogicWrappedBeanHandler implements WrappedBeanHandler {

    private static final Log log = Logs.getLog(WeblogicWrappedBeanHandler.class);
    private static final List<String> wrappedClassName = Arrays.asList("weblogic.ejb.container.internal.SessionEJBContextImpl");

    @Override
    public Object unwrap(Object bean) {
        try {
            return bean.getClass().getMethod("getBean").invoke(bean);
        } catch (Exception e) {
            log.warn("not weblogic wrap bean");
        }
        return null;
    }

    @Override
    public boolean isWrappedBean(Object bean) {
        Class<?> beanClass = bean.getClass();
        for (String className : wrappedClassName) {
            if (beanClass.getName().equals(className)) {
                return true;
            }
            try {
                Class<?> wrappedBeanClass = ClassUtils.forName(className);
                if (ClassUtils.isAssignable(wrappedBeanClass, beanClass)) {
                    return true;
                }
            } catch (Throwable e) {
            }
        }
        return false;
    }
}

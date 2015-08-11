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
package com.harmony.umbrella.context.ee.util;

import static com.harmony.umbrella.context.ApplicationMetadata.ServerInformation.*;

import java.lang.reflect.Constructor;
import java.util.Properties;

import com.harmony.umbrella.context.ApplicationMetadata.ServerInformation;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.context.ee.ContextConfigurationResolver;
import com.harmony.umbrella.context.ee.GenericContextResolver;
import com.harmony.umbrella.context.ee.resolver.GenericContextBeanResolver;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ContextManager {

    public static ContextConfigurationResolver getContextResolver(ServerInformation serverInfo, Properties props) {
        String resolverClassName = props.getProperty("jndi.context.resolver");
        if (resolverClassName != null) {
            return createResolverFromClassName(resolverClassName, serverInfo, props, ContextConfigurationResolver.class);
        }
        int serverType = serverInfo == null ? UNKNOW : serverInfo.serverType;
        switch (serverType) {
        case WEBLOGIC:
        case WEBSPHERE:
        case GLASSFISH:
        case JBOSS:
        case TOMCAT:
        default:
            return new GenericContextResolver(props);
        }
    }

    public static BeanResolver getContextBeanResolver(ServerInformation serverInfo, Properties props) {
        String resolverClassName = props.getProperty("jndi.context.resolver");
        if (resolverClassName != null) {
            return createResolverFromClassName(resolverClassName, serverInfo, props, BeanResolver.class);
        }
        int serverType = serverInfo == null ? UNKNOW : serverInfo.serverType;
        switch (serverType) {
        case WEBLOGIC:
            // return new WebLogicContextBeanResolver(props);
        case WEBSPHERE:
        case GLASSFISH:
        case JBOSS:
        case TOMCAT:
        default:
            return new GenericContextBeanResolver(props);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends ContextConfigurationResolver> T createResolverFromClassName(String resolverClassName, ServerInformation serverInfo, Properties props,
            Class<T> targetClass) {
        try {
            Class<?> resolverClass = Class.forName(resolverClassName);
            if (targetClass.isAssignableFrom(resolverClass)) {
                Constructor<?> constructor = resolverClass.getConstructor(Properties.class);
                if (constructor != null) {
                    return (T) constructor.newInstance(props);
                } else {
                    return (T) ReflectionUtils.instantiateClass(resolverClass);
                }
            }
            throw new IllegalArgumentException("class " + resolverClassName + " not assignable from " + targetClass.getName());
        } catch (Exception e) {
            throw new IllegalArgumentException("class " + resolverClassName + " cant't resolver", e);
        }
    }
}

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
package com.harmony.umbrella.ee;

import static com.harmony.umbrella.context.ApplicationMetadata.ServerInformation.*;

import java.lang.reflect.Constructor;
import java.util.Properties;

import com.harmony.umbrella.context.ApplicationMetadata.ServerInformation;
import com.harmony.umbrella.ee.resolver.ConfigurationBeanResolver;
import com.harmony.umbrella.ee.resolver.InternalContextResolver;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ResolverManager {

    /**
     * 创建beanResolver
     *
     * @param serverInfo
     *         服务的信息
     * @param props
     *         创建的附属资源
     * @return beanResolver
     */
    public static BeanResolver createBeanResolver(ServerInformation serverInfo, Properties props) {
        String resolverClassName = props.getProperty("jndi.context.resolver");
        if (StringUtils.isNotBlank(resolverClassName)) {
            return createResolver(resolverClassName, serverInfo, props, ContextResolver.class);
        }
        int serverType = serverInfo == null ? UNKNOW : serverInfo.serverType;
        switch (serverType) {
            case WEBLOGIC:
            case WEBSPHERE:
            case GLASSFISH:
            case JBOSS:
            case TOMCAT:
            default:
                return new ConfigurationBeanResolver(props);
        }
    }

    /**
     * 创建ContextResolver
     *
     * @param serverInfo
     *         服务的信息
     * @param props
     *         创建的附属资源
     * @return contextResolver
     */
    public static ContextResolver createContextResolver(ServerInformation serverInfo, Properties props) {
        String resolverClassName = props.getProperty("jndi.context.resolver");
        if (StringUtils.isNotBlank(resolverClassName)) {
            return createResolver(resolverClassName, serverInfo, props, ContextResolver.class);
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
                return new InternalContextResolver(props);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends ContextResolver> T createResolver(String resolverClassName, ServerInformation serverInfo,
                                                                Properties props, Class<T> targetClass) {
        try {
            Class<?> resolverClass = ClassUtils.forName(resolverClassName);
            if (targetClass.isAssignableFrom(resolverClass)) {
                Constructor<?> constructor;
                try {
                    constructor = resolverClass.getConstructor(Properties.class);
                    return (T) ReflectionUtils.instantiateClass(constructor, props);
                } catch (NoSuchMethodException e) {
                    return (T) ReflectionUtils.instantiateClass(resolverClass);
                }
            }
            throw new IllegalArgumentException("class " + resolverClassName + " not assignable from " + targetClass.getName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("class " + resolverClassName + " can not resolver", e);
        }
    }
}

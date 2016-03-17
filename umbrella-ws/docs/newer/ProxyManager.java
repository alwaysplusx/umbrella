/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.ws.newer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ContextVisitor;
import com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutor;
import com.harmony.umbrella.ws.jaxws.JaxWsServerBuilder;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;
import com.harmony.umbrella.ws.support.SimpleContext;

/**
 * @author wuxii@foxmail.com
 */
public class ProxyManager {

    private static final String address = "http://localhost:8080/hello";

    public static void main(String[] args) {
        JaxWsServerBuilder.create().publish(HelloWebService.class, address);
    }

    @Test
    public void testProxyManager() {
        HelloService service = create(HelloService.class);
        System.out.println(service.sayHi("wuxii"));
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> proxyClass) {
        if (isWebServiceInterface(proxyClass)) {
            ClassLoader cl = ClassUtils.getDefaultClassLoader();
            List<Class<?>> interfaces = new ArrayList<Class<?>>();
            interfaces.add(proxyClass);
            Collections.addAll(interfaces, proxyClass.getInterfaces());
            return (T) Proxy.newProxyInstance(cl, interfaces.toArray(new Class[interfaces.size()]), new ProxyHandler());
        }
        throw new ProxyNotFoundException("");
    }

    public static boolean isWebServiceInterface(Class<?> clazz) {
        return true;
    }

    private static class ProxyHandler implements InvocationHandler {

        private JaxWsExecutor executor = new JaxWsCXFExecutor();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (isWebMethod(method)) {
                return executor.execute(buildContext(method, args), generatorVisitor(method));
            } else if (ReflectionUtils.isObjectMethod(method)) {
                if (ReflectionUtils.isEqualsMethod(method)) {
                    return executor.equals(args[0]);
                } else if (ReflectionUtils.isHashCodeMethod(method)) {
                    return executor.hashCode();
                } else if (ReflectionUtils.isToStringMethod(method)) {
                    return executor.toString();
                }
            }

            throw new UnsupportedOperationException("");
        }

        private ContextVisitor[] generatorVisitor(Method method) {
            return new ContextVisitor[0];
        }

        public boolean isWebMethod(Method method) {
            return true;
        }

        private Context buildContext(Method method, Object[] arguments) {
            SimpleContext context = new SimpleContext(method.getDeclaringClass(), method.getName(), arguments);
            context.setAddress(address);
            return context;
        }

    }

}

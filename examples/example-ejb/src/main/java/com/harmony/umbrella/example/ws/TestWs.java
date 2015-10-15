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
package com.harmony.umbrella.example.ws;

import java.net.URL;
import java.util.Enumeration;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "TestWs")
@WebService(serviceName = "TestWs")
public class TestWs {

    public void test(String name) {

        try {
            Class.forName(TestWs.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            SimpleBeanFactory beanFactory = new SimpleBeanFactory();
            Object bean = beanFactory.getBean(TestWs.class.getName());
            System.out.println(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        System.out.println(classLoader);

        Class<?>[] classes = ResourceManager.getInstance().getClasses(name);
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }

    }

    public void test2() {
        ResourceManager.getInstance().cleanClass();
        ResourceManager.getInstance().clearResource();
    }

    public void test3(String name) throws Exception {
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
        URL url = cl.getResource(name);
        System.out.println(url == null ? "" : url);

        Enumeration<URL> resources = cl.getResources(name);
        while (resources.hasMoreElements()) {
            System.out.println(resources.nextElement());
        }

        ClassLoader libCl = SimpleBeanFactory.class.getClassLoader();
        System.out.println(libCl);

        ClassLoader clcl = TestWs.class.getClassLoader();
        Enumeration<URL> resources2 = clcl.getResources(name);
        while (resources2.hasMoreElements()) {
            System.out.println(resources2.nextElement());
        }

        resources2 = clcl.getParent().getResources(name);
        while (resources2.hasMoreElements()) {
            System.out.println(resources2.nextElement());
        }

        ClassLoader loader = ClassLoader.getSystemClassLoader();
        System.out.println(loader);
        
        Enumeration<URL> systemResources = ClassLoader.getSystemResources(name);
        while (systemResources.hasMoreElements()) {
            System.out.println(systemResources.nextElement());
        }

    }

}

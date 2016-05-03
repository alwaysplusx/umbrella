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

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
package com.harmony.umbrella.util;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * one class one jmx bean
 *
 * @author wuxii@foxmail.com
 */
public class JmxManager {

    public static final String DEFAULT_PREFIX = "com.harmony.umbrella.jmx";
    public static final String JMX_HTML_MBEAN_NAME = "com.harmony.umbrella.jmx:type=webConsole";
    private static final Logger log = LoggerFactory.getLogger(JmxManager.class);

    private static JmxManager instance;
    private final MBeanServer server;

    /**
     * 类和jmx objectName对应关系map
     */
    private Map<Class<?>, String> objNameMap = new HashMap<Class<?>, String>();

    private JmxManager() {
        server = ManagementFactory.getPlatformMBeanServer();
    }

    public static JmxManager getInstance() {
        if (instance == null) {
            synchronized (JmxManager.class) {
                if (instance == null) {
                    instance = new JmxManager();
                }
            }
        }
        return instance;
    }

    /**
     * 将一个对象注册到JMS中
     * <p/>
     * 自动生成一个objectName
     *
     * @param object
     *         待绑定的对象
     * @return 返回{@code true}绑定成功
     */
    public boolean registerMBean(Object object) {
        return registerMBean(object, toJMXObjectName(object.getClass()));
    }

    /**
     * 将一个对象注册到指定的objectName
     * <p/>
     * 注册mbean的最终入口
     *
     * @param object
     *         待绑定的对象
     * @param objectName
     *         绑定的名称
     * @return 返回{@code true}绑定成功
     */
    public boolean registerMBean(Object object, String objectName) {
        try {
            ObjectName objName = new ObjectName(objectName);
            server.registerMBean(object, objName);
            objNameMap.put(object.getClass(), objectName);
        } catch (MalformedObjectNameException e) {
            throw newMalformedObjectNameRuntimeException(e);
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
        return true;
    }

    /**
     * 反注册jmx对象
     *
     * @param clazz
     *         解除绑定
     * @return 返回{@code true}解除成功
     */
    public boolean unregisterMBean(Class<?> clazz) {
        return unregisterMBean(toJMXObjectName(clazz));
    }

    /**
     * 反注册objectName对象
     * <p/>
     * 反注册的最终入口
     *
     * @param objectName
     *         待解除的名称
     * @return 返回{@code true}解除成功
     */
    public boolean unregisterMBean(String objectName) {
        try {
            ObjectName name = new ObjectName(objectName);
            if (server.isRegistered(name)) {
                server.unregisterMBean(name);
                log.debug("unregister mbean {} successfully", name);
            } else {
                log.debug("mbean {} not register yet", name);
            }
            return true;
        } catch (MalformedObjectNameException e) {
            throw newMalformedObjectNameRuntimeException(e);
        } catch (Exception e) {
            log.error("unregister mbean {} failed", objectName, e);
        }
        return false;
    }

    /**
     * 查看对象是否已经注册
     *
     * @param clazz
     *         检查的对象类
     * @return 返回{@code true}表示已经注册
     */
    public boolean isRegistered(Class<?> clazz) {
        return isRegistered(toJMXObjectName(clazz));
    }

    /**
     * 查看objectName是否已经注册
     *
     * @param objectName
     *         检查的名称
     * @return 返回{@code true}表示已经注册
     */
    public boolean isRegistered(String objectName) {
        try {
            return server.isRegistered(new ObjectName(objectName));
        } catch (MalformedObjectNameException e) {
            throw newMalformedObjectNameRuntimeException(e);
        }
    }

    /**
     * 注册jmx http功能
     *
     * @param port
     *         注册网页适配
     * @return 返回{@code true}注册成功
     */
    public boolean registerHtmlAdaptorServer(int port) {
        if (!isRegistered(JMX_HTML_MBEAN_NAME)) {
            try {
                Class<?> adaptorClass = Class.forName("com.sun.jdmk.comm.HtmlAdaptorServer");
                Constructor<?> constructor = adaptorClass.getConstructor(Integer.TYPE);
                Object object = constructor.newInstance(port);
                Method startMethod = adaptorClass.getMethod("start", new Class[]{});
                startMethod.invoke(object);
                registerMBean(object, JMX_HTML_MBEAN_NAME);
                log.debug("register html adaptor {} successfully", JMX_HTML_MBEAN_NAME);
            } catch (Exception e) {
                log.debug("register html adaptor server failed", e);
                return false;
            }
        }
        return true;
    }

    protected String toJMXObjectName(Class<?> clazz) {
        StringBuffer sb = new StringBuffer();
        Package pkg = clazz.getPackage();
        if (pkg != null) {
            sb.append(pkg.getName());
        } else {
            sb.append(DEFAULT_PREFIX);
        }
        sb.append(":type=").append(clazz.getSimpleName());
        return sb.toString();
    }

    private static RuntimeException newMalformedObjectNameRuntimeException(Exception ex) {
        return new IllegalArgumentException("malformed object name", ex);
    }

}

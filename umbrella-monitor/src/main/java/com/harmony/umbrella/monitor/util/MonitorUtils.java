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
package com.harmony.umbrella.monitor.util;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.monitor.Graph.Level;
import com.harmony.umbrella.monitor.annotation.HttpProperty;
import com.harmony.umbrella.monitor.annotation.InternalProperty;
import com.harmony.umbrella.monitor.annotation.Monitored;
import com.harmony.umbrella.util.StringUtils;

/**
 * 监听工具类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class MonitorUtils {

    /**
     * 将方转化为唯一的资源限定标识
     * 
     * @param method
     *            方法
     * @return 生成方法的唯一id签名
     */
    public static String methodId(Method method) {
        return method == null ? "" : StringUtils.getMethodId(method);
    }

    /**
     * 将request转为唯一的资源限定标识
     * 
     * @param request
     *            http request
     * @return 请求的唯一id
     */
    public static String requestId(HttpServletRequest request) {
        return request == null ? null : request.getRequestURI();
    }

    /**
     * 获取监控方法的模块名称
     * 
     * @param clazz
     *            监控的类
     * @return 模块名称
     * @see Monitored#module()
     */
    public static String getModule(Class<?> clazz) {
        return getModule(getMonitored(clazz));
    }

    /**
     * 获取监控方法的模块名称
     * 
     * @param method
     *            监控的方法
     * @return 模块名称
     * @see Monitored#module()
     */
    public static String getModule(Method method) {
        return getModule(getMonitored(method));
    }

    private static String getModule(Monitored monitored) {
        return monitored != null ? monitored.module() : "";
    }

    /**
     * 获取监控方法的操作名称
     * 
     * @param clazz
     *            监控的类
     * @return 操作名称
     * @see Monitored#operator()
     */
    public static String getOperator(Class<?> clazz) {
        return getOperator(getMonitored(clazz));
    }

    /**
     * 获取监控方法的操作名称
     * 
     * @param method
     *            监控的方法
     * @return 操作名称
     * @see Monitored#operator()
     */
    public static String getOperator(Method method) {
        return getOperator(getMonitored(method));
    }

    private static String getOperator(Monitored monitored) {
        return monitored != null ? monitored.operator() : "";
    }

    /**
     * 获取监控方法的级别
     * 
     * @param clazz
     *            监控的类
     * @return 大类级别
     * @see Monitored#category()
     */
    public static String getCategory(Class<?> clazz) {
        return getCategory(getMonitored(clazz));
    }

    /**
     * 获取监控方法的级别
     * 
     * @param method
     *            监控的方法
     * @return 大类级别
     * @see Monitored#category()
     */
    public static String getCategory(Method method) {
        return getCategory(getMonitored(method));
    }

    private static String getCategory(Monitored monitored) {
        return monitored != null ? monitored.category() : "";
    }

    /**
     * 获取监控方法的日志级别
     * 
     * @param clazz
     *            监控的类
     * @return 日志级别
     * @see Monitored#level()
     */
    public static Level getLevel(Class<?> clazz) {
        return getLevel(getMonitored(clazz));
    }

    /**
     * 获取监控方法的日志级别
     * 
     * @param method
     *            监控的方法
     * @return 日志级别
     * @see Monitored#level()
     */
    public static Level getLevel(Method method) {
        Monitored monitored = getMonitored(method);
        return monitored != null ? monitored.level() : Level.INFO;
    }

    private static Level getLevel(Monitored monitored) {
        return monitored != null ? monitored.level() : Level.INFO;
    }

    /**
     * 获取监控方法的内部属性注解
     * 
     * @param clazz
     *            监控的类
     * @return 内部属性注解
     * @see Monitored#internalProperties()
     */
    public static InternalProperty[] getInternalProperty(Class<?> clazz) {
        return getInternalProperty(getMonitored(clazz));
    }

    /**
     * 获取监控方法的内部属性注解
     * 
     * @param method
     *            监控的方法
     * @return 内部属性注解
     * @see Monitored#internalProperties()
     */
    public static InternalProperty[] getInternalProperty(Method method) {
        return getInternalProperty(getMonitored(method));
    }

    private static InternalProperty[] getInternalProperty(Monitored monitored) {
        return monitored != null ? monitored.internalProperties() : new InternalProperty[0];
    }

    /**
     * 获取监控方法的http属性注解
     * 
     * @param clazz
     *            监控的类
     * @return http属性注解
     * @see Monitored#httpProperties()
     */
    public static HttpProperty[] getHttpProperty(Class<?> clazz) {
        return getHttpProperty(getMonitored(clazz));
    }

    /**
     * 获取监控方法的http属性注解
     * 
     * @param method
     *            监控的方法
     * @return http属性注解
     * @see Monitored#httpProperties()
     */
    public static HttpProperty[] getHttpProperty(Method method) {
        Monitored monitored = getMonitored(method);
        return monitored != null ? monitored.httpProperties() : new HttpProperty[0];
    }

    private static HttpProperty[] getHttpProperty(Monitored monitored) {
        return monitored != null ? monitored.httpProperties() : new HttpProperty[0];
    }

    /**
     * 从方法中获取监控注解, 如果没有找到返回null
     * 
     * @param method
     *            查找的方法
     * @return 监控注解
     */
    private static Monitored getMonitored(Method method) {
        return method.getAnnotation(Monitored.class);
    }

    /**
     * 从方法中获取监控注解, 如果没有找到返回null
     * 
     * @param clazz
     * @return
     */
    private static Monitored getMonitored(Class<?> clazz) {
        return clazz.getAnnotation(Monitored.class);
    }
}

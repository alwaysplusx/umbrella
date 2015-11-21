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

    public static String getModule(Method method) {
        Monitored monitored = getMonitored(method);
        return monitored != null ? monitored.module() : "";
    }

    public static String getOperator(Method method) {
        Monitored monitored = getMonitored(method);
        return monitored != null ? monitored.module() : "";
    }

    public static String getCategory(Method method) {
        Monitored monitored = getMonitored(method);
        return monitored != null ? monitored.module() : "";
    }

    public static Level getLevel(Method method) {
        Monitored monitored = getMonitored(method);
        return monitored != null ? monitored.level() : Level.INFO;
    }

    public static InternalProperty[] getInternalProperty(Method method) {
        Monitored monitored = getMonitored(method);
        return monitored != null ? monitored.internalProperties() : new InternalProperty[0];
    }

    public static HttpProperty[] getHttpProperty(Method method) {
        Monitored monitored = getMonitored(method);
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

}

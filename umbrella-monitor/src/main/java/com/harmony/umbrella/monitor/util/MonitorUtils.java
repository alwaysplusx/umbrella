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
     * @return
     */
    public static String methodIdentifie(Method method) {
        if (method == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (Class<?> clazz : method.getParameterTypes()) {
            sb.append(", ").append(clazz.getName());
        }
        if (sb.length() > 0) {
            sb.delete(0, 2);
        }
        return method.getDeclaringClass().getName() + "#(" + sb.toString() + ")";
    }

    /**
     * 将request转为唯一的资源限定标识
     * 
     * @param request
     * @return
     */
    public static String requestIdentifie(HttpServletRequest request) {
        if (request == null)
            return "";
        return request.getRequestURI();
    }

}

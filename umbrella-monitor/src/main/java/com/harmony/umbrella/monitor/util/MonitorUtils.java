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
     * @return
     */
    public static String methodId(Method method) {
        return method == null ? "" : StringUtils.getMethodId(method);
    }

    /**
     * 将request转为唯一的资源限定标识
     * 
     * @param request
     * @return
     */
    public static String requestId(HttpServletRequest request) {
        return request == null ? null : request.getRequestURI();
    }

}

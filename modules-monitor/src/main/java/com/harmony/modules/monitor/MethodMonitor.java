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
package com.harmony.modules.monitor;

import java.lang.reflect.Method;

import com.harmony.modules.monitor.support.MethodMonitorInterceptor.Monitored;

/**
 * 方法监视器
 * @author wuxii@foxmail.com
 */
public interface MethodMonitor extends Monitor {

    /**
     * 排除在监视名单外
     * @param method 方法
     */
    void exclude(Method method);

    /**
     * 包含在监视名单内
     * @param method 方法
     */
    void include(Method method);

    /**
     * 检测方法是否受到监视
     * @param method 方法
     * @return
     */
    boolean isMonitored(Method method);

    /**
     * 方法监视结果
     */
    public interface MethodGraph extends Graph {

        /**
         * 方法的执行目标
         * @return
         */
        Object getTarget();

        /**
         * 方法请求的参数
         * @return
         */
        Object[] getArgs();

        /**
         * 方法
         * @return
         */
        Method getMethod();

        /**
         * 方法请求时候发生的异常
         * @return
         */
        Exception getException();

        /**
         * 方法所属的模块
         * @return
         * @see Monitored#module()
         */
        String getModule();

        /**
         * 方法所对应的操作
         * @return
         * @see Monitored#operator()
         */
        String getOperator();

    }
}

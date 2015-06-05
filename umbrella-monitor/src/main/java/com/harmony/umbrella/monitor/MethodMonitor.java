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
package com.harmony.umbrella.monitor;

import java.lang.reflect.Method;
import java.util.Collection;

import com.harmony.umbrella.Constant;
import com.harmony.umbrella.monitor.support.MethodExpressionMatcher;

/**
 * 方法监控器
 * <p>
 * 默认监控符合表达式{@linkplain #DEFAULT_METHOD_PATTERN}的方法.
 * <p>
 * 该表达式解析如 {@linkplain MethodExpressionMatcher}
 * 
 * @author wuxii@foxmail.com
 * 
 */
public interface MethodMonitor extends Monitor<Method> {

    String DEFAULT_METHOD_PATTERN = "execution(* " + Constant.DEFAULT_PACKAGE + "..*.*(..))";

    /**
     * 方法监控结果
     */
    public interface MethodGraph extends Graph<Collection<Object>> {

        /**
         * 方法的执行目标
         * 
         * @return
         */
        Object getTarget();

        /**
         * 方法请求的参数
         * 
         * @return
         */
        Object[] getArguments();

        /**
         * 方法
         * 
         * @return
         */
        Method getMethod();

    }

}

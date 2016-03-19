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
package com.harmony.umbrella.monitor;

import java.lang.reflect.Method;

/**
 * 方法监控结果视图
 */
public interface MethodGraph extends Graph {
    /**
     * method 拦截内部属性
     */
    String METHOD_PROPERTY = MethodGraph.class.getName() + ".METHOD_PROPERTY";
    /**
     * 拦截方法的返回值
     */
    String METHOD_RESULT = MethodGraph.class.getName() + ".METHOD_RESULT";
    /**
     * 拦截方法的请求参数
     */
    String METHOD_ARGUMENT = MethodGraph.class.getName() + ".METHOD_ARGUMENT";

    /**
     * 方法的执行目标
     * 
     * @return target
     */
    Object getTarget();

    /**
     * 获取目标类
     * 
     * @return target class
     */
    Class<?> getTargetClass();

    /**
     * 拦截的方法
     * 
     * @return method
     */
    Method getMethod();

    /**
     * 拦截方法的请求参数
     * 
     * @return 方法的参数
     */
    Object[] getMethodArguments();

    /**
     * 方法的返回值
     * 
     * @return 返回值
     */
    Object getMethodResult();

}
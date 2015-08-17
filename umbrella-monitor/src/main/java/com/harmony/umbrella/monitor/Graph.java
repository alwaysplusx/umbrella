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

import java.util.Calendar;
import java.util.Map;

/**
 * 监控记录
 * 
 * @author wuxii@foxmail.com
 */
public interface Graph {

    /**
     * 监控的资源标识
     * 
     * @return 资源id
     */
    String getIdentifier();

    /**
     * 请求时间
     * 
     * @return 请求时间
     */
    Calendar getRequestTime();

    /**
     * 系统应答时间
     * 
     * @return 应答时间
     */
    Calendar getResponseTime();

    /**
     * 请求耗时 单位毫秒(ms)
     * 
     * @return -1 无法计算耗时
     */
    long use();

    /**
     * 返回结果
     * 
     * @return 监控返回的结果
     */
    Map<String, Object> getResult();

    /**
     * 得到唯一的结果
     * 
     * @return 监控结果
     */
    Object getSingleResult();

    /**
     * 请求参数
     * 
     * @return 请求参数
     */
    Map<String, Object> getArguments();

    /**
     * 请求中是否有异常
     * 
     * @return true is exception
     */
    boolean isException();

    /**
     * 异常的原因
     * 
     * @return exception, if not exception return null
     */
    Exception getException();

    /**
     * 监控的整体描述
     * 
     * @return 监控的描述
     */
    String getDescription();

}

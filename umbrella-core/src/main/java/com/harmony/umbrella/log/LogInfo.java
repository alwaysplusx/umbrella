/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.log;

import java.util.Date;
import java.util.Map;

/**
 * 日志信息Bean
 * 
 * @author wuxii@foxmail.com
 */
public interface LogInfo {

    /**
     * 日志模块
     * 
     * @return
     */
    String getModule();

    /**
     * 操作名称
     * 
     * @return
     */
    String getAction();

    /**
     * 日志消息内容
     * 
     * @return
     */
    Message getMessage();

    /**
     * 日志的异常信息
     * 
     * @return
     */
    Throwable getException();

    /**
     * 日志级别
     * 
     * @return
     */
    Level getLevel();

    /**
     * 业务日志的结果
     * 
     * @return
     */
    Object getResult();

    /**
     * 记录开始事件
     * 
     * @return
     */
    Date getStartTime();

    /**
     * 记录结束事件
     * 
     * @return
     */
    Date getFinishTime();

    /**
     * 操作人
     * 
     * @return
     */
    String getOperator();

    /**
     * 操作人id
     * 
     * @return
     */
    Object getOperatorId();

    /**
     * 业务日志模块
     * 
     * @return
     */
    String getBizModule();

    /**
     * 业务日志数据主键
     * 
     * @return
     */
    Object getBizId();

    /**
     * 检查是否有异常
     * 
     * @return
     */
    boolean isException();

    /**
     * 记录耗时
     * 
     * @return
     */
    long use();

    /**
     * 操作栈，操作位于程序的位置
     * 
     * @return
     */
    String getStack();

    /**
     * 操作的线程
     * 
     * @return
     */
    String getThreadName();

    Map<String, Object> getContext();

}

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
package com.harmony.modules.scheduling.jmx;

/**
 * 发布JMS MBean接口
 * 
 * @author wuxii@foxmail.com
 */
public interface JMXScheduleMBean {

    /**
     * 重新启动所有定时任务
     */
    void restartAll();

    /**
     * 启动所有定时任务
     */
    void startAll();

    /**
     * 关闭并移除所有定时任务
     */
    void stopAll();

    /**
     * 恢复所有挂起的定时任务
     */
    void resumeAll();

    /**
     * 挂起所有的定时任务
     */
    void pauseAll();

    /**
     * 重启定时任务
     * 
     * @param jobName
     *            任务的job名称
     */
    void restart(String jobName);

    /**
     * 启动定时任务
     * 
     * @param jobName
     *            任务的job名称
     */
    void start(String jobName);

    /**
     * 关闭并移除定时任务
     * 
     * @param jobName
     *            任务的job名称
     */
    void stop(String jobName);

    /**
     * 挂起定时任务，定时器空跑不执行job
     * 
     * @param jobName
     *            任务的job名称
     */
    void pause(String jobName);

    /**
     * 恢复定时任务
     * 
     * @param jobName
     *            任务的job名称
     */
    void resume(String jobName);
}

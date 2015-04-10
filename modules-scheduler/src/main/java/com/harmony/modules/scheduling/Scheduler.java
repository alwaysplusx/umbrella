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
package com.harmony.modules.scheduling;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 定时任务调度者
 * 
 * @author wuxii@foxmail.com
 */
public interface Scheduler {

    /**
     * 重新启动所有定时任务
     */
    void restartAll() throws SchedulerException;

    /**
     * 启动所有定时任务
     */
    void startAll() throws SchedulerException;

    /**
     * 关闭并移除所有定时任务
     */
    void stopAll() throws SchedulerException;

    /**
     * 恢复所有挂起的定时任务
     */
    void resumeAll() throws SchedulerException;

    /**
     * 挂起所有的定时任务
     */
    void pauseAll() throws SchedulerException;

    /**
     * 重启定时任务
     * 
     * @param jobName
     *            任务的job名称
     */
    void restart(String pause) throws SchedulerException;

    /**
     * 启动定时任务
     * 
     * @param jobName
     *            任务的job名称
     */
    void start(String jobName) throws SchedulerException;

    /**
     * 关闭并移除定时任务
     * 
     * @param jobName
     *            任务的job名称
     */
    void stop(String jobName) throws SchedulerException;

    /**
     * 挂起定时任务，定时器空跑不执行job
     * 
     * @param jobName
     *            任务的job名称
     */
    void pause(String jobName) throws SchedulerException;

    /**
     * 恢复定时任务
     * 
     * @param jobName
     *            任务的job名称
     */
    void resume(String jobName) throws SchedulerException;

    /**
     * 查看当前任务是否处于准备状态
     * 
     * @param jobName
     * @return
     */
    boolean isReady(String jobName);

    /**
     * 查看当前任务是否启动
     * 
     * @param jobName
     *            任务的job名称
     * @return true启动,false未启动
     */
    boolean isStarted(String jobName);

    /**
     * 查看当前任务是否停止
     * 
     * @param jobName
     * @return
     */
    boolean isStopped(String jobName);

    /**
     * 查看当前任务是否被挂起
     * 
     * @param jobName
     * @return
     */
    boolean isPaused(String jobName);

    /**
     * 是否存在改任务
     * 
     * @param jobName
     *            任务的job名称
     * @return true存在, false不存在
     */
    boolean hasJob(String jobName);

    /**
     * 获取job的运行状态
     * 
     * @param jobName
     * @return
     */
    Status getJobStatus(String jobName);

    /**
     * 获取job的运行情况
     * 
     * @param jobName
     *            任务的job名称
     * @return job的运行情况
     */
    JobInfo getJobInfo(String jobName);

    /**
     * 定时任务Job所处在的状态
     */
    public enum Status {
        READY, START, PAUSE, STOP;
    }

    /**
     * job的执行情况，常存于内存中
     */
    interface JobInfo extends Serializable {

        /**
         * job的名称
         * 
         * @return
         */
        String getJobName();

        /**
         * 等级时间，Job注册在Scheduler的时间
         * 
         * @return
         */
        Calendar getRegisterTime();

        /**
         * scheduler中启动该job的时间
         * 
         * @return
         */
        Calendar getStartTime();

        /**
         * 最近执行开始时间
         * 
         * @return
         */
        Calendar getLastExecuteStartTime();

        /**
         * 最近执行完成时间
         * 
         * @return
         */
        Calendar getLastExecuteFinishTime();

        /**
         * 最近执行出现异常的时间
         * 
         * @return
         */
        Calendar getLastExceptionTime();

        /**
         * 被挂起的次数
         * 
         * @return
         */
        int getPauseTimes();

        /**
         * 平均执行间隔
         * 
         * @return
         */
        float getAverageInterval();

        /**
         * 执行成功的次数
         * 
         * @return
         */
        int getSuccessTimes();

        /**
         * 执行的次数
         * 
         * @return
         */
        int getExecuteTimes();

        /**
         * 执行异常的次数
         * 
         * @return
         */
        int getExceptionTimes();

        /**
         * 关联的job的class
         * 
         * @return
         */
        Class<? extends Job> getJobClass();

        /**
         * job的当前状态
         * 
         * @return
         */
        Status getJobStatus();

        /**
         * 设置运行状态
         * 
         * @param status
         * @return
         */
        void setJobStatus(Status status);

        /**
         * job触发的规则
         * 
         * @return
         */
        Trigger getJobTrigger();

        /**
         * 执行job的定时器
         * 
         * @return
         */
        Object getTimer();

        /**
         * 最近异常提示消息
         * 
         * @return
         */
        String getLastExceptionMessage();

        /**
         * 记录备份
         */
        void dump();
    }

}

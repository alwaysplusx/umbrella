package com.harmony.umbrella.scheduling;

import com.harmony.umbrella.scheduling.Scheduler.JobInfo;

/**
 * job类,所有的定时任务实现这个类
 * 
 * @author wuxii@foxmail.com
 */
public interface Job {

    /**
     * 执行job,定时器会定时调用该方法
     * 
     * @param jobInfo
     *            当前定时器的运行情况
     */
    void process(JobInfo jobInfo);

}

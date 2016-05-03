package com.harmony.umbrella.scheduling.jmx;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.scheduling.Scheduler;
import com.harmony.umbrella.scheduling.SchedulerException;

/**
 * 发布JMX的辅助类
 * 
 * @author wuxii@foxmail.com
 * @see Scheduler
 *
 */
public class JMXSchedule implements JMXScheduleMBean {

    private static final Log log = Logs.getLog(JMXSchedule.class);
    private Scheduler scheduler;

    @Override
    public void restartAll() {
        try {
            scheduler.restartAll();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void startAll() {
        try {
            scheduler.startAll();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void stopAll() {
        try {
            scheduler.stopAll();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void resumeAll() {
        try {
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void pauseAll() {
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void restart(String jobName) {
        try {
            scheduler.restart(jobName);
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void start(String jobName) {
        try {
            scheduler.start(jobName);
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void stop(String jobName) {
        try {
            scheduler.stop(jobName);
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void pause(String jobName) {
        try {
            scheduler.pause(jobName);
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    @Override
    public void resume(String jobName) {
        try {
            scheduler.resume(jobName);
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

}

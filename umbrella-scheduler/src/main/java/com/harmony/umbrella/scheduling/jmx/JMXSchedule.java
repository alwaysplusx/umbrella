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
package com.harmony.umbrella.scheduling.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(JMXSchedule.class);
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

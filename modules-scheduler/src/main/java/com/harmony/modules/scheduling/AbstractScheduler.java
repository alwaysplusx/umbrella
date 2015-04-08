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
package com.harmony.modules.scheduling;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractScheduler<T extends Scheduler.JobInfo> implements Scheduler {

    protected static final Logger log = LoggerFactory.getLogger(AbstractScheduler.class);
    protected Map<String, T> jobInfoMap = new HashMap<String, T>();

    protected abstract void doStart(String jobName, T jobInfo);

    protected abstract void doStop(String jobName, T jobInfo);

    protected abstract void doPause(String jobName, T jobInfo);

    protected void doResume(String jobName, T jobInfo) {
        // empty implement
    }

    @Override
    public void start(String jobName) {
        if (hasJob(jobName) && !isStarted(jobName)) {
            T jobInfo = jobInfoMap.get(jobName);
            doStart(jobName, jobInfo);
            jobInfo.setJobStatus(Status.START);
            return;
        }
        log.info("can't start job {} not find", jobName);
    }

    @Override
    public void stop(String jobName) {
        if (hasJob(jobName) && !isStopped(jobName)) {
            T jobInfo = jobInfoMap.get(jobName);
            doStop(jobName, jobInfo);
            jobInfo.setJobStatus(Status.STOP);
            return;
        }
        log.info("can't stop job {} not find", jobName);
    }

    @Override
    public void pause(String jobName) {
        if (hasJob(jobName) && !isPaused(jobName)) {
            T jobInfo = jobInfoMap.get(jobName);
            doPause(jobName, jobInfo);
            jobInfo.setJobStatus(Status.PAUSE);
            return;
        }
        log.info("can't pause job {} not find", jobName);
    }

    @Override
    public void resume(String jobName) {
        if (hasJob(jobName) && isPaused(jobName)) {
            T jobInfo = jobInfoMap.get(jobName);
            doResume(jobName, jobInfo);
            jobInfo.setJobStatus(Status.START);
            return;
        }
        log.info("can't resume job {} not find", jobName);
    }

    @Override
    public void restart(String jobName) {
        stop(jobName);
        start(jobName);
    }

    @Override
    public void restartAll() {
        stopAll();
        startAll();
    }

    @Override
    public void startAll() {
        for (String jobName : getJobNames()) {
            start(jobName);
        }
    }

    @Override
    public void stopAll() {
        for (String jobName : getJobNames()) {
            stop(jobName);
        }
    }

    @Override
    public void resumeAll() {
        for (String jobName : getJobNames()) {
            resume(jobName);
        }
    }

    @Override
    public void pauseAll() {
        for (String jobName : getJobNames()) {
            pause(jobName);
        }
    }

    public String[] getJobNames() {
        Set<String> names = jobInfoMap.keySet();
        return names.toArray(new String[names.size()]);
    }

    public JobInfo getJobInfo(String jobName) {
        return jobInfoMap.get(jobName);
    }

    @Override
    public boolean isReady(String jobName) {
        return Status.READY.equals(getJobStatus(jobName));
    }

    @Override
    public boolean isStarted(String jobName) {
        return Status.START.equals(getJobStatus(jobName));
    }

    @Override
    public boolean isStopped(String jobName) {
        return Status.STOP.equals(getJobStatus(jobName));
    }

    @Override
    public boolean isPaused(String jobName) {
        return Status.PAUSE.equals(getJobStatus(jobName));
    }

    @Override
    public Status getJobStatus(String jobName) {
        JobInfo jobInfo = jobInfoMap.get(jobName);
        return jobInfo != null ? jobInfo.getJobStatus() : null;
    }

    @Override
    public boolean hasJob(String jobName) {
        return jobInfoMap.containsKey(jobName);
    }

}

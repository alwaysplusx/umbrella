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
package com.harmony.umbrella.scheduling.support;

import java.io.IOException;
import java.util.Properties;

import com.harmony.umbrella.scheduling.AbstractScheduler;
import com.harmony.umbrella.scheduling.Job;
import com.harmony.umbrella.scheduling.Scheduler;
import com.harmony.umbrella.scheduling.SchedulerException;
import com.harmony.umbrella.scheduling.Trigger;
import com.harmony.umbrella.util.PropUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractPropertiesFileScheduler<T extends Scheduler.JobInfo> extends AbstractScheduler<T> {

    public static final String JOB_PROPERTIES_FILE_LOCATION = "META-INF/scheduler/jobs.properties";

    public static final String TRIGGER_PROPERTIES_FILE_LOCATION = "META-INF/scheduler/triggers.properties";

    protected final String triggerPropertiesFile;
    protected final String jobPropertiesFile;

    public AbstractPropertiesFileScheduler(String triggerPropertiesFile, String jobPropertiesFile) {
        this.triggerPropertiesFile = triggerPropertiesFile;
        this.jobPropertiesFile = jobPropertiesFile;
    }

    protected abstract Class<? extends Job> findJobClass(String jobClassName);

    protected abstract JobInfo initializeJobInfo(String name, Class<? extends Job> jobClass);

    @Override
    protected void init() throws SchedulerException {
        try {
            Properties jobProps = PropUtils.loadProperties(jobPropertiesFile);
            for (String name : jobProps.stringPropertyNames()) {
                String jobClassName = jobProps.getProperty(name);
                if (StringUtils.isEmpty(jobClassName)) {
                    throw new IllegalArgumentException("job[" + name + "] class cannot be null");
                }
                initializeJobInfo(name, findJobClass(jobClassName));
            }
        } catch (IOException e) {
            throw new SchedulerException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 根据jobName加载对应的{@linkplain Trigger}
     * 
     * @param jobName
     * @return
     */
    protected Trigger getJobTrigger(String jobName) {
        try {
            Properties props = PropUtils.loadProperties(triggerPropertiesFile);
            String triggerExpression = props.getProperty(jobName);
            return triggerExpression != null ? new ExpressionTrigger(triggerExpression) : null;
        } catch (IOException e) {
        }
        return null;
    }
}

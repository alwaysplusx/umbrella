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
package com.harmony.umbrella.scheduling.ejb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import com.harmony.umbrella.core.BeanLoader;
import com.harmony.umbrella.core.ClassBeanLoader;
import com.harmony.umbrella.scheduling.JobEntry;
import com.harmony.umbrella.scheduling.Scheduler;
import com.harmony.umbrella.scheduling.Trigger;
import com.harmony.umbrella.scheduling.support.DefaultJobEntry;
import com.harmony.umbrella.scheduling.support.ExpressionTrigger;
import com.harmony.umbrella.util.PropUtils;

/**
 * 基于配置文件的定时任务管理
 * 
 * @author wuxii@foxmail.com
 */
@Stateless
@Remote(Scheduler.class)
public class PropertiesFileEJBScheduler extends AbstractEJBScheduler {

    public static final String jobPropertiesFileLocation = "META-INF/scheduler/jobs.properties";

    public static final String triggerPropertiesFileLocation = "META-INF/scheduler/triggers.properties";

    private String jobPropertiesFile = jobPropertiesFileLocation;

    private String triggerPropertiesFile = triggerPropertiesFileLocation;

    @Resource
    private TimerService timerService;

    private BeanLoader beanLoader = new ClassBeanLoader();

    @Override
    protected TimerService getTimerService() {
        return timerService;
    }

    @Override
    protected BeanLoader getBeanLoader() {
        return beanLoader;
    }

    @Override
    @Timeout
    protected void monitorTask(Timer timer) {
        handle(timer);
    }

    @Override
    protected Trigger getJobTrigger(String jobName) {
        try {
            Properties props = PropUtils.loadProperties(triggerPropertiesFile);
            String triggerExpression = props.getProperty(jobName);
            return triggerExpression != null ? new ExpressionTrigger(triggerExpression) : null;
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    protected List<? extends JobEntry> getAllJobEntry() {
        List<DefaultJobEntry> list = new ArrayList<DefaultJobEntry>();
        try {
            Properties jobProps = PropUtils.loadProperties(jobPropertiesFile);
            for (String name : jobProps.stringPropertyNames()) {
                list.add(new DefaultJobEntry(name, jobProps.getProperty(name)));
            }
        } catch (IOException e) {
        }
        return list;
    }
}

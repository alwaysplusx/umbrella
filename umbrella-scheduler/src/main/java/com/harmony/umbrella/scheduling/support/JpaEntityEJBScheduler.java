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

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import com.harmony.umbrella.core.BeanLoader;
import com.harmony.umbrella.core.ClassBeanLoader;
import com.harmony.umbrella.scheduling.Scheduler;
import com.harmony.umbrella.scheduling.SchedulerException;
import com.harmony.umbrella.scheduling.Trigger;

/**
 * TODO 未完成
 * @author wuxii@foxmail.com
 */
@Stateless
@Remote(Scheduler.class)
public class JpaEntityEJBScheduler extends AbstractEJBScheduler {

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
    protected void init() throws SchedulerException {
    }

    @Override
    @Timeout
    protected void monitorTask(Timer timer) {
        handle(timer);
    }

    @Override
    protected Trigger getJobTrigger(String jobName) {
        return null;
    }

}

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
package com.harmony.umbrella.scheduling;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import com.harmony.umbrella.scheduling.AbstractEJBScheduler;
import com.harmony.umbrella.scheduling.JobFactory;

/**
 * @author wuxii@foxmail.com
 */
@Stateless
public class EJBSchedulerWrapper extends AbstractEJBScheduler {

    @Resource
    private TimerService timerService;
    private AbstractEJBScheduler ejbScheduler;

    @PostConstruct
    public void postConstruct() {
        // TODO init scheduler
    }

    @Override
    protected TimerService getTimerService() {
        return ejbScheduler.getTimerService();
    }

    @Override
    protected void monitorTask(Timer timer) {
        ejbScheduler.monitorTask(timer);
    }

    @Override
    protected JobFactory getJobFactory() {
        return ejbScheduler.getJobFactory();
    }

}

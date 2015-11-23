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

import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.scheduling.AbstractEJBScheduler;
import com.harmony.umbrella.scheduling.JobFactory;
import com.harmony.umbrella.scheduling.ext.JpaJobFactory;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class JpaEntityEJBScheduler extends AbstractEJBScheduler {

    private final TimerService timerService;
    private JobFactory jobFactory;

    public JpaEntityEJBScheduler(TimerService timerService, EntityManager em) {
        this.timerService = timerService;
        this.jobFactory = new JpaJobFactory(em);
    }

    @Override
    protected TimerService getTimerService() {
        return timerService;
    }

    @Override
    protected void monitorTask(Timer timer) {
        handle(timer);
    }

    @Override
    protected JobFactory getJobFactory() {
        return jobFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        ((JpaJobFactory) getJobFactory()).setBeanFactory(beanFactory);
    }
}

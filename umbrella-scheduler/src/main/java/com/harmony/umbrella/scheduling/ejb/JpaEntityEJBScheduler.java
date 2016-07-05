package com.harmony.umbrella.scheduling.ejb;

import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;

import com.harmony.umbrella.beans.BeanFactory;
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

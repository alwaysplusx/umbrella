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

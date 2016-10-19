package com.harmony.umbrella.scheduling.ejb;

import javax.ejb.Timer;
import javax.ejb.TimerService;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.scheduling.AbstractEJBScheduler;
import com.harmony.umbrella.scheduling.JobFactory;
import com.harmony.umbrella.scheduling.support.PropertiesFileJobFactory;

/**
 * 基于配置文件的定时任务管理
 * 
 * @author wuxii@foxmail.com
 */
public class PropertiesFileEJBScheduler extends AbstractEJBScheduler {

    private final TimerService timerService;
    private JobFactory jobFactory;

    public PropertiesFileEJBScheduler(TimerService timerService) {
        this.timerService = timerService;
        this.jobFactory = new PropertiesFileJobFactory();
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
        ((PropertiesFileJobFactory) getJobFactory()).setBeanFactory(beanFactory);
    }

}

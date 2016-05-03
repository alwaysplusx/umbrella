package com.harmony.umbrella.scheduling;

/**
 * 定时任务自定义异常
 * 
 * @author wuxii@foxmail.com
 */
public class SchedulerException extends Exception {

    private static final long serialVersionUID = -726485722525497476L;

    public SchedulerException() {
        super();
    }

    public SchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchedulerException(String message) {
        super(message);
    }

    public SchedulerException(Throwable cause) {
        super(cause);
    }

}

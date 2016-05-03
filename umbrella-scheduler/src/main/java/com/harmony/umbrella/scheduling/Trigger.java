package com.harmony.umbrella.scheduling;

/**
 * 定时任务trigger
 * 
 * @author wuxii@foxmail.com
 *
 */
public interface Trigger {

    /**
     * 年
     */
    public String getYears();

    /**
     * 月
     */
    public String getMonths();

    /**
     * 一个月中的第几天
     */
    public String getDayOfMonth();

    /**
     * 一周中的第几天
     */
    public String getDayOfWeek();

    /**
     * 时间
     */
    public String getHours();

    /**
     * 分钟
     */
    public String getMinutes();

    /**
     * 秒
     */
    public String getSeconds();

    /**
     * 启动延时
     * 
     * @return
     */
    public long getDelay();

}
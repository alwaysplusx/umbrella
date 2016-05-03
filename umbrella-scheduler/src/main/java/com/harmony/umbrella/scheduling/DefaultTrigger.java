package com.harmony.umbrella.scheduling;

import java.io.Serializable;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultTrigger implements Trigger, Serializable {

    private static final long serialVersionUID = 1106913448650629166L;
    protected String seconds;
    protected String minutes;
    protected String hours;
    protected String dayOfMonth;
    protected String months;
    protected String dayOfWeek;
    protected String years;
    protected long delay;

    @Override
    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    @Override
    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    @Override
    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    @Override
    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    @Override
    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    @Override
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    @Override
    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dayOfMonth == null) ? 0 : dayOfMonth.hashCode());
        result = prime * result + ((dayOfWeek == null) ? 0 : dayOfWeek.hashCode());
        result = prime * result + (int) (delay ^ (delay >>> 32));
        result = prime * result + ((hours == null) ? 0 : hours.hashCode());
        result = prime * result + ((minutes == null) ? 0 : minutes.hashCode());
        result = prime * result + ((months == null) ? 0 : months.hashCode());
        result = prime * result + ((seconds == null) ? 0 : seconds.hashCode());
        result = prime * result + ((years == null) ? 0 : years.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultTrigger other = (DefaultTrigger) obj;
        if (dayOfMonth == null) {
            if (other.dayOfMonth != null)
                return false;
        } else if (!dayOfMonth.equals(other.dayOfMonth))
            return false;
        if (dayOfWeek == null) {
            if (other.dayOfWeek != null)
                return false;
        } else if (!dayOfWeek.equals(other.dayOfWeek))
            return false;
        if (delay != other.delay)
            return false;
        if (hours == null) {
            if (other.hours != null)
                return false;
        } else if (!hours.equals(other.hours))
            return false;
        if (minutes == null) {
            if (other.minutes != null)
                return false;
        } else if (!minutes.equals(other.minutes))
            return false;
        if (months == null) {
            if (other.months != null)
                return false;
        } else if (!months.equals(other.months))
            return false;
        if (seconds == null) {
            if (other.seconds != null)
                return false;
        } else if (!seconds.equals(other.seconds))
            return false;
        if (years == null) {
            if (other.years != null)
                return false;
        } else if (!years.equals(other.years))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "{\"seconds\":\"" + seconds + "\", \"minutes\":\"" + minutes + "\", \"hours\":\"" + hours + "\", \"dayOfMonth\":\"" + dayOfMonth
                + "\", \"months\":\"" + months + "\", \"dayOfWeek\":\"" + dayOfWeek + "\", \"years\":\"" + years + "\", \"delay\":\"" + delay + "\"}";
    }
}

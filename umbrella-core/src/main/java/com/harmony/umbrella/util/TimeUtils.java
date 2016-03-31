package com.harmony.umbrella.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间的工具类,方法不足可参阅{@link Calendar}或者{org.apache.commons.lang.time}包下类.
 * 
 * @author Hal
 * @version 1.0 ,before 2012-9-12
 * @see java.util.Calendar
 * @see SimpleDateFormat
 */
public class TimeUtils {

    // 时间转换 TODO 允许空输入, 输入空返回空
    public static Date toDate(String text, String pattern) throws ParseException {
        return Formats.createDateFormat(pattern).parseDate(text);
    }

    public static Date toDate(Calendar date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    public static Date toDate(long time) {
        return new Date(time);
    }

    public static Calendar toCalendar(String text, String pattern) throws ParseException {
        return Formats.createDateFormat(pattern).parseCalendar(text);
    }

    public static Calendar toCalendar(Date date) {
        if (date == null) {
            return null;
        }
        return toCalendar(date.getTime());
    }

    public static Calendar toCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public static String parseText(Date date, String pattern) {
        return Formats.createDateFormat(pattern).format(date);
    }

    public static String parseText(Calendar date, String pattern) {
        return Formats.createDateFormat(pattern).format(date);
    }

    // java.util.Date 时间截取
    public static int getYear(Date date) {
        return 1;
    }

    public static int getMonth(Date date) {
        return 1;
    }

    public static int getDayOfYear(Date date) {
        return 1;
    }

    public static int getDayOfMonth(Date date) {
        return 1;
    }

    public static int getDayOfWeek(Date date) {
        return 1;
    }

    public static int getHour(Date date) {
        return 1;
    }

    public static int getMinute(Date date) {
        return 1;
    }

    public static int getSecond(Date date) {
        return 1;
    }

    // java.util.Calendar 时间截取
    public static int getYear(Calendar date) {
        return 1;
    }

    public static int getMonth(Calendar date) {
        return 1;
    }

    public static int getDayOfYear(Calendar date) {
        return 1;
    }

    public static int getDayOfMonth(Calendar date) {
        return 1;
    }

    public static int getDayOfWeek(Calendar date) {
        return 1;
    }

    public static int getHour(Calendar date) {
        return 1;
    }

    public static int getMinute(Calendar date) {
        return 1;
    }

    public static int getSecond(Calendar date) {
        return 1;
    }

    public static long get(Date date, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return get(calendar, field);
    }

    public static long get(Calendar c, int field) {
        return c.get(field);
    }

    // 时间转换
    public static long second(Date date) {
        return 1;
    }

    public static long second(Calendar date) {
        return 1;
    }

    public static long millisecond(Date date) {
        return 1;
    }

    public static long millisecond(Calendar date) {
        return 1;
    }

    // 计算时间间隔
    public static long intervalSecond(Date begin, Date end) {
        return interval(begin, end, TimeUnit.SECONDS);
    }

    public static long intervalMilliseconds(Date begin, Date end) {
        return interval(begin, end, TimeUnit.SECONDS);
    }

    public static long interval(Date begin, Date end, TimeUnit timeUnit) {
        return timeUnit.convert(end.getTime() - begin.getTime(), TimeUnit.MILLISECONDS);
    }

    public static long interval(Calendar begin, Calendar end, TimeUnit timeUnit) {
        return timeUnit.convert(end.getTimeInMillis() - begin.getTimeInMillis(), TimeUnit.MILLISECONDS);
    }

    // 时间的加减

    public static void addYear(Date date, int amount) {
        add(date, Calendar.YEAR, amount);
    }

    public static void addMonth(Date date, int amount) {
        add(date, Calendar.MONTH, amount);
    }

    public static void addDay(Date date, int amount) {
        add(date, Calendar.DAY_OF_YEAR, amount);
    }

    public static void addWeek(Date date, int amount) {
        add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    public static void addHour(Date date, int amount) {
        add(date, Calendar.HOUR, amount);
    }

    public static void addMinute(Date date, int amount) {
        add(date, Calendar.MINUTE, amount);
    }

    public static void addSecond(Date date, int amount) {
        add(date, Calendar.SECOND, amount);
    }

    public static void addMillisecond(Date date, int amount) {
        add(date, Calendar.MILLISECOND, amount);
    }

    public static void addYear(Calendar date, int amount) {
        add(date, Calendar.YEAR, amount);
    }

    public static void addMonth(Calendar date, int amount) {
        add(date, Calendar.MONTH, amount);
    }

    public static void addWeek(Calendar date, int amount) {
        add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    public static void addDay(Calendar date, int amount) {
        add(date, Calendar.DAY_OF_YEAR, amount);
    }

    public static void addHour(Calendar date, int amount) {
        add(date, Calendar.HOUR, amount);
    }

    public static void addMinute(Calendar date, int amount) {
        add(date, Calendar.MINUTE, amount);
    }

    public static void addSecond(Calendar date, int amount) {
        add(date, Calendar.SECOND, amount);
    }

    public static void addMillisecond(Calendar date, int amount) {
        add(date, Calendar.MILLISECOND, amount);
    }

    public static void add(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        add(calendar, field, amount);
        date.setTime(calendar.getTimeInMillis());
    }

    public static void add(Calendar date, int field, int amount) {
        date.add(field, amount);
    }

}

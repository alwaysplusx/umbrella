package com.harmony.umbrella.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 */
public class TimeUtils {

    /**
     * 将时间文本按格式转化为时间对象
     * 
     * @param text
     *            时间文本
     * @param pattern
     *            格式
     * @return 时间对象 {@linkplain java.util.Date}
     * @throws ParseException
     *             文本与格式不匹配
     */
    public static Date toDate(String text, String pattern) throws ParseException {
        return Formats.createDateFormat(pattern).parseDate(text);
    }

    /**
     * {@linkplain Calendar}转换为{@linkplain Date}
     * 
     * @param date
     *            calendar
     * @return date
     */
    public static Date toDate(Calendar date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    /**
     * 将时间与1970, 00:00:00 GMT毫秒间隔转化为时间对象
     * 
     * @param time
     *            毫秒
     * @return Date
     */
    public static Date toDate(long time) {
        return new Date(time);
    }

    /**
     * 将时间文本按格式转化为时间对象Calendar
     * 
     * @param text
     *            时间文本
     * @param pattern
     *            格式
     * @return 时间对象 {@linkplain java.util.Date}
     * @throws ParseException
     *             文本与格式不匹配
     */
    public static Calendar toCalendar(String text, String pattern) throws ParseException {
        return Formats.createDateFormat(pattern).parseCalendar(text);
    }

    /**
     * Date -> Calendar
     * 
     * @param date
     *            Date
     * @return Calendar
     */
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
        return getYear(toCalendar(date));
    }

    public static int getMonth(Date date) {
        return getMonth(toCalendar(date));
    }

    public static int getDayOfYear(Date date) {
        return getDayOfYear(toCalendar(date));
    }

    public static int getDayOfMonth(Date date) {
        return getDayOfMonth(toCalendar(date));
    }

    public static int getDayOfWeek(Date date) {
        return getDayOfWeek(toCalendar(date));
    }

    public static int getHour(Date date) {
        return getHour(toCalendar(date));
    }

    public static int getMinute(Date date) {
        return getMinute(toCalendar(date));
    }

    public static int getSecond(Date date) {
        return getSecond(toCalendar(date));
    }

    // java.util.Calendar 时间截取
    public static int getYear(Calendar date) {
        return get(date, Calendar.YEAR);
    }

    public static int getMonth(Calendar date) {
        return get(date, Calendar.MONTH);
    }

    public static int getDayOfYear(Calendar date) {
        return get(date, Calendar.DAY_OF_YEAR);
    }

    public static int getDayOfMonth(Calendar date) {
        return get(date, Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek(Calendar date) {
        return get(date, Calendar.DAY_OF_WEEK);
    }

    public static int getHour(Calendar date) {
        return get(date, Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Calendar date) {
        return get(date, Calendar.MINUTE);
    }

    public static int getSecond(Calendar date) {
        return get(date, Calendar.SECOND);
    }

    public static long get(Date date, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return get(calendar, field);
    }

    public static int get(Calendar c, int field) {
        return c.get(field);
    }

    // 时间转换
    public static long second(Date date) {
        return TimeUnit.MILLISECONDS.toSeconds(date.getTime());
    }

    public static long second(Calendar date) {
        return TimeUnit.MILLISECONDS.toSeconds(date.getTimeInMillis());
    }

    public static long millisecond(Date date) {
        return date.getTime();
    }

    public static long millisecond(Calendar date) {
        return date.getTimeInMillis();
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
        add(date, Calendar.HOUR_OF_DAY, amount);
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

    public static void add(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        add(calendar, field, amount);
        date.setTime(calendar.getTimeInMillis());
    }

    // calendar

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
        add(date, Calendar.HOUR_OF_DAY, amount);
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

    public static void add(Calendar date, int field, int amount) {
        date.add(field, amount);
    }

}

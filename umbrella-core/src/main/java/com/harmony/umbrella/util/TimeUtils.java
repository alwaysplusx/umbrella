package com.harmony.umbrella.util;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static Date toDate(LocalDate localDate) {
        return toDate(localDate, ZoneId.systemDefault());
    }

    public static Date toDate(LocalDate localDate, ZoneId zoneId) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(zoneId == null ? ZoneId.systemDefault() : zoneId).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return toDate(localDateTime, ZoneId.systemDefault());
    }

    public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(zoneId == null ? ZoneId.systemDefault() : zoneId).toInstant());
    }

    /**
     * 按系统指定的格式化模版组合格式化日志
     *
     * @param text
     * @return
     * @throws ParseException
     */
    public static Date toDate(String text) throws ParseException {
        Date date = null;
        for (String pattern : Formats.DATA_PATTERNS) {
            try {
                date = toDate(text, pattern);
            } catch (ParseException e) {
            }
            if (date != null) {
                break;
            }
        }
        if (date == null) {
            throw new ParseException(text, 0);
        }
        return date;
    }

    public static Calendar toCalendar(String text) throws ParseException {
        return toCalendar(toDate(text));
    }

    /**
     * 将时间文本按格式转化为时间对象
     *
     * @param text    时间文本
     * @param pattern 格式
     * @return 时间对象 {@linkplain java.util.Date}
     * @throws ParseException 文本与格式不匹配
     */
    public static Date toDate(String text, String pattern) throws ParseException {
        return Formats.createDateFormat(pattern).parseDate(text);
    }

    /**
     * {@linkplain Calendar}转换为{@linkplain Date}
     *
     * @param date calendar
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
     * @param time 毫秒
     * @return Date
     */
    public static Date toDate(long time) {
        return new Date(time);
    }

    /**
     * 将时间文本按格式转化为时间对象Calendar
     *
     * @param text    时间文本
     * @param pattern 格式
     * @return 时间对象 {@linkplain java.util.Date}
     * @throws ParseException 文本与格式不匹配
     */
    public static Calendar toCalendar(String text, String pattern) throws ParseException {
        return Formats.createDateFormat(pattern).parseCalendar(text);
    }

    /**
     * Date -> Calendar
     *
     * @param date Date
     * @return Calendar
     */
    public static Calendar toCalendar(Date date) {
        if (date == null) {
            return null;
        }
        return toCalendar(date.getTime());
    }

    /**
     * 毫秒->Calendar
     *
     * @param time 毫秒数
     * @return Calendar
     */
    public static Calendar toCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    /**
     * 按时间格式模版转化为文本事件
     *
     * @param date    时间
     * @param pattern 时间模版
     * @return 时间文本
     */
    public static String formatText(Date date, String pattern) {
        return Formats.createDateFormat(pattern).format(date);
    }

    /**
     * 按时间格式模版转化为文本事件
     *
     * @param date    时间
     * @param pattern 时间模版
     * @return 时间文本
     */
    public static String formatText(Calendar date, String pattern) {
        return Formats.createDateFormat(pattern).format(date);
    }

    // java.util.Date 时间截取

    /**
     * 截取时间的年份
     *
     * @param date 时间
     * @return 年份
     */
    public static int getYear(Date date) {
        return getYear(toCalendar(date));
    }

    /**
     * 截取时间的月份
     *
     * @param date 时间
     * @return 月份
     */
    public static int getMonth(Date date) {
        return getMonth(toCalendar(date));
    }

    /**
     * 获取时间按是一年中的第几天
     *
     * @param date 时间
     * @return day of year
     */
    public static int getDayOfYear(Date date) {
        return getDayOfYear(toCalendar(date));
    }

    /**
     * 获取时间是一个月的第几天
     *
     * @param date 时间
     * @return day of month
     */
    public static int getDayOfMonth(Date date) {
        return getDayOfMonth(toCalendar(date));
    }

    /**
     * 获取时间是一周中的第几天
     *
     * <pre>
     * 星期一 = 1
     * 星期二 = 2
     * ...
     * 星期天 = 7
     * </pre>
     *
     * @param date 时间
     * @return 星期几
     */
    public static int getDayOfWeek(Date date) {
        return getDayOfWeek(toCalendar(date));
    }

    /**
     * 获取时间的小时部分, 24小时制
     *
     * @param date 时间
     * @return 小时时间24小时制
     */
    public static int getHour(Date date) {
        return getHour(toCalendar(date));
    }

    /**
     * 截取时间的分钟
     *
     * @param date 时间
     * @return 分钟
     */
    public static int getMinute(Date date) {
        return getMinute(toCalendar(date));
    }

    /**
     * 截取时间的秒数
     *
     * @param date 时间
     * @return 秒数
     */
    public static int getSeconds(Date date) {
        return getSeconds(toCalendar(date));
    }

    // java.util.Calendar 时间截取

    /**
     * 截取时间的年份
     *
     * @param date 时间
     * @return 年份
     */
    public static int getYear(Calendar date) {
        return get(date, Calendar.YEAR);
    }

    /**
     * 截取时间的月份
     *
     * @param date 时间
     * @return 月份
     */
    public static int getMonth(Calendar date) {
        return get(date, Calendar.MONTH);
    }

    /**
     * 获取时间按是一年中的第几天
     *
     * @param date 时间
     * @return day of year
     */
    public static int getDayOfYear(Calendar date) {
        return get(date, Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取时间是一个月的第几天
     *
     * @param date 时间
     * @return day of month
     */
    public static int getDayOfMonth(Calendar date) {
        return get(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取时间是一周中的第几天
     *
     * <pre>
     * 星期一 = 1
     * 星期二 = 2
     * ...
     * 星期天 = 7
     * </pre>
     *
     * @param date 时间
     * @return 星期几
     */
    public static int getDayOfWeek(Calendar date) {
        return get(date, Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取时间的小时部分, 24小时制
     *
     * @param date 时间
     * @return 小时时间24小时制
     */
    public static int getHour(Calendar date) {
        return get(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 截取时间的分钟
     *
     * @param date 时间
     * @return 分钟
     */
    public static int getMinute(Calendar date) {
        return get(date, Calendar.MINUTE);
    }

    /**
     * 截取时间的秒数
     *
     * @param date 时间
     * @return 秒数
     */
    public static int getSeconds(Calendar date) {
        return get(date, Calendar.SECOND);
    }

    /**
     * 截取时间的指定值
     *
     * @param date 时间
     * @return 时间指定值
     * @see Calendar#get(int)
     */
    public static long get(Date date, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return get(calendar, field);
    }

    /**
     * 截取时间的指定值
     *
     * @param c 时间
     * @return 时间指定值
     * @see Calendar#get(int)
     */
    public static int get(Calendar c, int field) {
        return c.get(field);
    }

    // 时间转换

    /**
     * 将时间转为秒数, 秒数为与 January 1, 1970, 00:00:00 时间间隔
     *
     * @param date 时间
     * @return 秒数
     */
    public static long toSeconds(Date date) {
        return TimeUnit.MILLISECONDS.toSeconds(date.getTime());
    }

    /**
     * 将时间转为秒数, 秒数为与 January 1, 1970, 00:00:00 时间间隔
     *
     * @param date 时间
     * @return 秒数
     */
    public static long toSeconds(Calendar date) {
        return TimeUnit.MILLISECONDS.toSeconds(date.getTimeInMillis());
    }

    /**
     * 将时间转为毫秒数, 毫秒数为与 January 1, 1970, 00:00:00 时间间隔
     *
     * @param date 时间
     * @return 毫秒数
     */
    public static long toMillis(Date date) {
        return date.getTime();
    }

    /**
     * 将时间转为毫秒数, 毫秒数为与 January 1, 1970, 00:00:00 时间间隔
     *
     * @param date 时间
     * @return 毫秒数
     */
    public static long toMillis(Calendar date) {
        return date.getTimeInMillis();
    }

    // 计算时间间隔

    /**
     * 计算两个时间的时间间隔秒数
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 时间秒数
     */
    public static long intervalSeconds(Date begin, Date end) {
        return interval(begin, end, TimeUnit.SECONDS);
    }

    /**
     * 计算两个时间的时间间隔毫秒数
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 时间毫秒数
     */
    public static long intervalMillis(Date begin, Date end) {
        return interval(begin, end, TimeUnit.SECONDS);
    }

    /**
     * 计算时间的时间间隔, 间隔的单位为指定的TimeUnit
     *
     * @param begin    开始时间
     * @param end      结束时间
     * @param timeUnit 时间单位
     * @return 间隔时间
     */
    public static long interval(Date begin, Date end, TimeUnit timeUnit) {
        return timeUnit.convert(end.getTime() - begin.getTime(), TimeUnit.MILLISECONDS);
    }

    /**
     * 计算时间的时间间隔, 间隔的单位为指定的TimeUnit
     *
     * @param begin    开始时间
     * @param end      结束时间
     * @param timeUnit 时间单位
     * @return 间隔时间
     */
    public static long interval(Calendar begin, Calendar end, TimeUnit timeUnit) {
        return timeUnit.convert(end.getTimeInMillis() - begin.getTimeInMillis(), TimeUnit.MILLISECONDS);
    }

    // 时间的加减

    /**
     * 给当前时间加/减(加为正,减为负)年份
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Date addYear(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)月份
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Date addMonth(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)周
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Date addWeek(Date date, int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)天
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Date addDay(Date date, int amount) {
        return add(date, Calendar.DAY_OF_YEAR, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)小时
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Date addHour(Date date, int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)分钟
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Date addMinute(Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)秒数
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Date addSeconds(Date date, int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)毫秒数
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Date addMillis(Date date, int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    // calendar

    /**
     * 给当前时间加/减(加为正,减为负)年份
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Calendar addYear(Calendar date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)月份
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Calendar addMonth(Calendar date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)周
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Calendar addWeek(Calendar date, int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)天
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Calendar addDay(Calendar date, int amount) {
        return add(date, Calendar.DAY_OF_YEAR, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)小时
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Calendar addHour(Calendar date, int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)分钟
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */

    public static Calendar addMinute(Calendar date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)秒数
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */

    public static Calendar addSeconds(Calendar date, int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * 给当前时间加/减(加为正,减为负)毫秒数
     *
     * @param date   当前时间
     * @param amount 增加或减少的时间
     */
    public static Calendar addMillis(Calendar date, int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    private static Calendar add(Calendar date, int field, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTimeInMillis());
        c.add(field, amount);
        return c;
    }

    private static Date add(final Date date, int field, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, amount);
        return c.getTime();
    }
}

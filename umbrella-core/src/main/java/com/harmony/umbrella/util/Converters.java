package com.harmony.umbrella.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wuxii@foxmail.com
 */
public class Converters {

    private static Date format(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        Date date = null;
        for (String pattern : Formats.DATA_PATTERNS) {
            try {
                date = TimeUtils.toDate(text, pattern);
                return date;
            } catch (ParseException e) {
            }
        }
        return date;
    }

    public static final class StringToDateConverter implements Converter<String, Date> {

        public StringToDateConverter() {
        }

        @Override
        public Date convert(String t) {
            Date date = format(t);
            if (date == null) {
                throw new IllegalArgumentException("illegal date format " + t);
            }
            return date;
        }

    }

    public static final class StringToCalendarConverter implements Converter<String, Calendar> {

        @Override
        public Calendar convert(String t) {
            Date date = format(t);
            if (date == null) {
                throw new IllegalArgumentException("illegal date format " + t);
            }
            return TimeUtils.toCalendar(date);
        }

    }

    public static final class StringToIntegerConverter implements Converter<String, Integer> {

        @Override
        public Integer convert(String t) {
            return Integer.valueOf(t);
        }

    }

    public static final class StringToDoubleConverter implements Converter<String, Double> {

        @Override
        public Double convert(String t) {
            return Double.valueOf(t);
        }

    }

    public static final class StringToLongConverter implements Converter<String, Long> {

        @Override
        public Long convert(String t) {
            return Long.valueOf(t);
        }

    }

    public static final class StringToFloatConverter implements Converter<String, Float> {

        @Override
        public Float convert(String t) {
            return Float.valueOf(t);
        }

    }

    public static final class StringToBooleanConverter implements Converter<String, Boolean> {

        @Override
        public Boolean convert(String t) {
            return Boolean.valueOf(t);
        }

    }

}

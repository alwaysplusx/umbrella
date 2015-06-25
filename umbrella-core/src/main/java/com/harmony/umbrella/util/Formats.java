/*
 * Copyright 2013-2015 wuxii@foxmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间格式化工具
 * 
 * @author wuxii@foxmail.com
 */
public abstract class Formats {

    public static final String FULL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String SHORT_DATE_PATTERN = "yyyy-MM-dd";

    public static final String DIAGONAL_DATE_PATTERN = "dd/MM/yyyy";

    public static final String COMMON_DATE_PATTERN = "yyyyMMdd";

    public static final String DEFAULT_NUMBER_PATTERN = "#.##";

    private final static Map<String, NullableDateFormat> dateFormatMap = new HashMap<String, NullableDateFormat>();

    private final static Map<String, NullableNumberFormat> numberFormatMap = new HashMap<String, NullableNumberFormat>();

    private static final DecimalFormat sdf = new DecimalFormat("#");

    /**
     * 创建一个日期格式工具
     * 
     * @param pattern
     *            格式
     * @return
     */
    public static NullableDateFormat createDateFormat(String pattern) {
        NullableDateFormat ndf = dateFormatMap.get(pattern);
        if (ndf == null) {
            dateFormatMap.put(pattern, ndf = new NullableDateFormat(pattern));
        }
        return ndf;
    }

    /**
     * 创建数字格式化工具
     * 
     * @param pattern
     *            模版
     * @param mode
     *            进制
     * @return
     */
    public static NullableNumberFormat createNumberFormat(String pattern, RoundingMode mode) {
        String key = pattern + "." + mode;
        NullableNumberFormat ndf = numberFormatMap.get(key);
        if (ndf == null) {
            numberFormatMap.put(key, ndf = new NullableNumberFormat(pattern, mode));
        }
        return ndf;
    }

    /**
     * 格式化为Long
     */
    public static Long longValue(Number number, RoundingMode mode) {
        if (number == null)
            return null;
        RoundingMode oldMode = sdf.getRoundingMode();
        try {
            synchronized (sdf) {
                sdf.setRoundingMode(mode);
                return Long.valueOf(sdf.format(number));
            }
        } finally {
            sdf.setRoundingMode(oldMode);
        }
    }

    public static Long longValue(String source, RoundingMode mode) throws ParseException {
        if (source == null)
            return null;
        return longValue(sdf.parse(source), mode);
    }

    public static Integer intValue(Number number, RoundingMode mode) {
        if (number == null)
            return null;
        RoundingMode oldMode = sdf.getRoundingMode();
        try {
            synchronized (sdf) {
                sdf.setRoundingMode(mode);
                return Integer.valueOf(sdf.format(number));
            }
        } finally {
            sdf.setRoundingMode(oldMode);
        }
    }

    public static Integer intValue(String source, RoundingMode mode) throws ParseException {
        if (source == null)
            return null;
        return intValue(sdf.parse(source), mode);
    }

    private abstract static class NullableFormat extends Format {

        private static final long serialVersionUID = 7554718413792962086L;

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            if (obj == null)
                return toAppendTo;
            return getFormat().format(obj, toAppendTo, pos);
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (source == null)
                return null;
            return getFormat().parseObject(source, pos);
        }

        protected abstract Format getFormat();

    }

    public static class NullableNumberFormat extends NullableFormat implements Serializable {

        private static final long serialVersionUID = 8770260343737030870L;
        private final String pattern;
        private final RoundingMode mode;
        private final DecimalFormat df;

        public NullableNumberFormat(String pattern) {
            this(pattern, RoundingMode.HALF_UP);
        }

        public NullableNumberFormat(String pattern, RoundingMode mode) {
            this.pattern = pattern;
            this.mode = mode;
            this.df = new DecimalFormat(pattern);
        }

        @Override
        protected Format getFormat() {
            return this.df;
        }

        /**
         * 格式化Number
         */
        public String format(Number number) {
            if (number == null)
                return null;
            return df.format(number);
        }

        /**
         * 格式化为Double
         */
        public Double doubleValue(Number number) {
            String str = format(number);
            return str != null ? Double.valueOf(str) : null;
        }

        public Long longValue(Number number) {
            return Formats.longValue(number, mode);
        }

        /**
         * 格式化为BigDecimal
         */
        public BigDecimal bigDecimalValue(Number number) {
            String str = format(number);
            return str != null ? new BigDecimal(str) : null;
        }

        /**
         * 数字的字符转为Number
         */
        public Number parse(String source) throws ParseException {
            if (source == null || "".equals(source))
                return null;
            return df.parse(df.format(df.parse(source)));
        }

        /**
         * 数字的字符转为Double
         */
        public Double parseDouble(String source) throws ParseException {
            return doubleValue(parse(source));
        }

        /**
         * 数字的字符转为BigDecimal
         */
        public BigDecimal parseBigDecimal(String source) throws ParseException {
            return bigDecimalValue(parse(source));
        }

        /**
         * 数字字符转为Long
         */
        public Long parseLong(String source) throws ParseException {
            return longValue(parse(source));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mode == null) ? 0 : mode.hashCode());
            result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
            NullableNumberFormat other = (NullableNumberFormat) obj;
            if (mode != other.mode)
                return false;
            if (pattern == null) {
                if (other.pattern != null)
                    return false;
            } else if (!pattern.equals(other.pattern))
                return false;
            return true;
        }

    }

    /**
     * 值可为空的格式化工具
     * 
     * @author wuxii@foxmail.com
     */
    public static class NullableDateFormat extends NullableFormat implements Serializable {

        private static final long serialVersionUID = 8448875239037856747L;
        private final SimpleDateFormat sdf;
        private final String pattern;

        public NullableDateFormat(String pattern) {
            this.pattern = pattern;
            this.sdf = new SimpleDateFormat(pattern);
        }

        @Override
        protected Format getFormat() {
            return this.sdf;
        }

        /**
         * @param obj
         * @return
         * @throws IllegalArgumentException
         *             如果不是{@linkplain Date}或者{@linkplain Calendar}
         */
        /*public String format(Object obj) {
            if (obj instanceof Date) {
                return format((Date) obj);
            } else if (obj instanceof Calendar) {
                return format((Calendar) obj);
            } else {
                throw new IllegalArgumentException("unsupport format type of " + obj);
            }
        }*/

        /**
         * 格式化时间
         * 
         * @param date
         * @return
         * @see {@linkplain java.text.SimpleDateFormat#format(Date)}
         */
        public String format(Date date) {
            if (date == null)
                return null;
            synchronized (sdf) {
                return sdf.format(date);
            }
        }

        /**
         * 格式化时间
         * 
         * @param c
         * @return
         */
        public String format(Calendar c) {
            if (c == null)
                return null;
            synchronized (sdf) {
                return sdf.format(c.getTime());
            }
        }

        /**
         * 将Date转为Calendar
         * 
         * @param date
         *            Date
         * @return
         */
        public Calendar toCalendar(Date date) {
            if (date == null)
                return null;
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c;
        }

        /**
         * 将Calendar转为Date
         * 
         * @param c
         *            Calendar
         * @return
         */
        public Date toDate(Calendar c) {
            if (c == null)
                return null;
            return c.getTime();
        }

        /**
         * 解析时间
         * 
         * @param source
         * @return
         * @throws ParseException
         * @see {@linkplain java.text.SimpleDateFormat#parse(String)}
         */
        public Date parseDate(String source) throws ParseException {
            if (StringUtils.isBlank(source))
                return null;
            synchronized (sdf) {
                return sdf.parse(source);
            }
        }

        /**
         * 解析时间
         * 
         * @param source
         * @return
         * @throws ParseException
         * @see {@linkplain java.text.SimpleDateFormat#parse(String)}
         */
        public Calendar parseCalendar(String source) throws ParseException {
            Date date = parseDate(source);
            if (date != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                return c;
            }
            return null;
        }

        /**
         * 格式
         * 
         * @return
         */
        public String getPattern() {
            return pattern;
        }

    }
}

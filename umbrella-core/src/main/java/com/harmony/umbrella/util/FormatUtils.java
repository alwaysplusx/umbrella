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
import java.text.ParseException;
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
public abstract class FormatUtils {

    public static final String FULL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String SHORT_DATE_PATTERN = "yyyy-MM-dd";

    public static final String DIAGONAL_DATE_PATTERN = "dd/MM/yyyy";

    public static final String COMMON_DATE_PATTERN = "yyyyMMdd";

    public static final String DEFAULT_NUMBER_PATTERN = "#.##";

    private final static Map<String, NullableDateFormat> dateFormatMap = new HashMap<String, NullableDateFormat>();

    private final static Map<String, NullableDecimalFormat> numFormatMap = new HashMap<String, NullableDecimalFormat>();

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

    public static NullableDecimalFormat createNumberFormat(String pattern, RoundingMode mode) {
        String key = pattern + "." + mode;
        NullableDecimalFormat ndf = numFormatMap.get(key);
        if (ndf == null) {
            numFormatMap.put(key, ndf = new NullableDecimalFormat(pattern, mode));
        }
        return ndf;
    }

    public static class NullableDecimalFormat implements Serializable {

        private static final long serialVersionUID = 8770260343737030870L;
        private final String pattern;
        private final RoundingMode mode;
        private final DecimalFormat df;

        public NullableDecimalFormat(String pattern) {
            this(pattern, RoundingMode.HALF_UP);
        }

        public NullableDecimalFormat(String pattern, RoundingMode mode) {
            this.pattern = pattern;
            this.mode = mode;
            this.df = new DecimalFormat(pattern);
        }

        public String format(Number number) {
            if (number == null)
                return null;
            return df.format(number);
        }

        public Long formatLong(Number number) {
            if (number == null)
                return null;
            try {
                return parseLong(String.valueOf(number));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Double formatDouble(Number number) {
            if (number == null)
                return null;
            try {
                return parseDouble(String.valueOf(number));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public BigDecimal formatBigDecimal(Number number) {
            if (number == null)
                return null;
            try {
                return parseBigDecimal(String.valueOf(number));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Number parse(String source) throws ParseException {
            if (source == null || "".equals(source))
                return null;
            return df.parse(source);
        }

        public Long parseLong(String source) throws ParseException {
            if (source == null || "".equals(source))
                return null;
            return df.parse(source).longValue();
        }

        public Double parseDouble(String source) throws ParseException {
            if (source == null || "".equals(source))
                return null;
            return df.parse(source).doubleValue();
        }

        public BigDecimal parseBigDecimal(String source) throws ParseException {
            if (source == null || "".equals(source))
                return null;
            return BigDecimal.valueOf(df.parse(source).doubleValue());
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
            NullableDecimalFormat other = (NullableDecimalFormat) obj;
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

    public static class NullableDateFormat implements Serializable {

        private static final long serialVersionUID = 8448875239037856747L;
        private final SimpleDateFormat sdf;
        private final String pattern;

        public NullableDateFormat(String pattern) {
            this.pattern = pattern;
            this.sdf = new SimpleDateFormat(pattern);
        }

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

        public Calendar toCalendar(Date date) {
            if (date == null)
                return null;
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c;
        }

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
            if (source == null || "".equals(source))
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
            Calendar c = Calendar.getInstance();
            Date date = parseDate(source);
            if (date != null) {
                c.setTime(date);
                return c;
            }
            return null;
        }

        public String getPattern() {
            return pattern;
        }

    }
}

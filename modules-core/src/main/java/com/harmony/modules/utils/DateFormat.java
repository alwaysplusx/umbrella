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
package com.harmony.modules.utils;

import java.io.Serializable;
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
public abstract class DateFormat {

    /**
     * 完整时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static final NullAbleDateFormat FULL_DATEFORMAT = new NullAbleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    /**
     * 默认时间格式 yyyy-MM-dd HH:mm
     */
    public static final NullAbleDateFormat DEFAULT_DATEFORMAT = new NullAbleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 简短时间格式，到天 yyyy-MM-dd
     */
    public static final NullAbleDateFormat SHORT_DATEFORMAT = new NullAbleDateFormat("yyyy-MM-dd");
    /**
     * 时间格式仅有年
     */
    public static final NullAbleDateFormat YEAR_DATEFORMAT = new NullAbleDateFormat("yyyy");

    /**
     * dd/MM/yyyy
     */
    public static final NullAbleDateFormat SHORT_DIAGONAL_DATEFORMAT = new NullAbleDateFormat("dd/MM/yyyy");

    /**
     * dd/MM/yyyy HH:mm:ss
     */
    public static final NullAbleDateFormat FULL_DIAGONAL_DATEFORMAT = new NullAbleDateFormat("dd/MM/yyyy HH:mm:ss");

    /**
     * yyyyMMdd
     */
    public static final NullAbleDateFormat SHORT_COMMON_DATEFORMAT = new NullAbleDateFormat("yyyyMMdd");

    private final static Map<String, NullAbleDateFormat> patternMap = new HashMap<String, NullAbleDateFormat>();

    static {
        patternMap.put(FULL_DATEFORMAT.pattern, FULL_DATEFORMAT);
        patternMap.put(DEFAULT_DATEFORMAT.pattern, DEFAULT_DATEFORMAT);
        patternMap.put(SHORT_DATEFORMAT.pattern, SHORT_DATEFORMAT);
        patternMap.put(YEAR_DATEFORMAT.pattern, YEAR_DATEFORMAT);
        patternMap.put(SHORT_DIAGONAL_DATEFORMAT.pattern, SHORT_DIAGONAL_DATEFORMAT);
        patternMap.put(FULL_DIAGONAL_DATEFORMAT.pattern, FULL_DIAGONAL_DATEFORMAT);
        patternMap.put(SHORT_COMMON_DATEFORMAT.pattern, SHORT_COMMON_DATEFORMAT);
    }

    /**
     * 创建一个日期格式工具
     * 
     * @param pattern
     *            格式
     * @return
     */
    public static NullAbleDateFormat create(String pattern) {
        NullAbleDateFormat ndf = patternMap.get(pattern);
        if (ndf == null) {
            patternMap.put(pattern, ndf = new NullAbleDateFormat(pattern));
        }
        return ndf;
    }

    public static class NullAbleDateFormat implements Serializable {

        private static final long serialVersionUID = 8448875239037856747L;
        private final SimpleDateFormat sdf;
        private final String pattern;

        public NullAbleDateFormat(String pattern) {
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

        /**
         * 解析时间
         * 
         * @param source
         * @return
         * @throws ParseException
         * @see {@linkplain java.text.SimpleDateFormat#parse(String)}
         */
        public Date parse(String source) throws ParseException {
            if (source == null || source.equals(""))
                return null;
            synchronized (sdf) {
                return sdf.parse(source);
            }
        }
    }
}

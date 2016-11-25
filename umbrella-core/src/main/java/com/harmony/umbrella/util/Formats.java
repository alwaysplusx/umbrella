package com.harmony.umbrella.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 时间格式化工具
 * 
 * @author wuxii@foxmail.com
 */
public class Formats {

    public static final String FULL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String SHORT_DATE_PATTERN = "yyyy-MM-dd";

    public static final String DIAGONAL_DATE_PATTERN = "dd/MM/yyyy";

    public static final String COMMON_DATE_PATTERN = "yyyyMMdd";

    static final Set<String> DATA_PATTERNS = new HashSet<String>();

    static {
        DATA_PATTERNS.add(FULL_DATE_PATTERN);
        DATA_PATTERNS.add(DEFAULT_DATE_PATTERN);
        DATA_PATTERNS.add(SHORT_DATE_PATTERN);
        DATA_PATTERNS.add(DIAGONAL_DATE_PATTERN);
        DATA_PATTERNS.add(COMMON_DATE_PATTERN);
        DATA_PATTERNS.add(COMMON_DATE_PATTERN);
        String patterns = Environments.getProperty("umbrella.date.pattern");
        if (StringUtils.isNotBlank(patterns)) {
            DATA_PATTERNS.addAll(Arrays.asList(patterns.split(",")));
        }
    }

    public static final String DEFAULT_NUMBER_PATTERN = "#.##";

    /**
     * 创建一个日期格式工具
     * 
     * @param pattern
     *            格式
     * @return 输入可为空的日期格式化工具
     */
    public static NullableDateFormat createDateFormat(String pattern) {
        return new NullableDateFormat(pattern);
    }

    /**
     * 根据配置信息穿件数字格式化工具
     * 
     * @param NumberFormatConfig
     *            数字格式化配置
     * @return 输入可为空的数字格式化工具
     */
    public static NullableNumberFormat createNumberFormat(NumberFormatConfig NumberFormatConfig) {
        return new NullableNumberFormat(NumberFormatConfig);
    }

    private abstract static class NullableFormat extends Format {

        private static final long serialVersionUID = 7554718413792962086L;

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            if (obj == null) {
                return toAppendTo;
            }
            return getFormat().format(obj, toAppendTo, pos);
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (source == null) {
                return null;
            }
            return getFormat().parseObject(source, pos);
        }

        protected abstract Format getFormat();

    }

    public static class NumberFormatConfig {

        private static final String PATTERN = "#.##";

        public static final NumberFormatConfig HALF_UP_2;

        public static final NumberFormatConfig HALF_UP_4;

        public static final NumberFormatConfig HALF_UP_6;

        static {
            HALF_UP_2 = new NumberFormatConfig(PATTERN, 2, RoundingMode.HALF_UP);
            HALF_UP_4 = new NumberFormatConfig(PATTERN, 4, RoundingMode.HALF_UP);
            HALF_UP_6 = new NumberFormatConfig(PATTERN, 6, RoundingMode.HALF_UP);
        }

        public final String pattern;
        public final int scale;
        public final RoundingMode roundingMode;

        public final int minimumIntegerDigits;
        public final int maximumIntegerDigits;
        public final int maximumFractionDigits;
        public final int minimumFractionDigits;

        public NumberFormatConfig(int scale, RoundingMode roundingMode) {
            this(PATTERN, scale, roundingMode, -1, -1, -1, -1);
        }

        public NumberFormatConfig(String pattern, int scale, RoundingMode roundingMode) {
            this(pattern, scale, roundingMode, -1, -1, -1, -1);
        }

        public NumberFormatConfig(String pattern, RoundingMode roundingMode, int maximumIntegerDigits, int maximumFractionDigits) {
            this.pattern = pattern;
            this.roundingMode = roundingMode;
            this.scale = maximumFractionDigits;
            this.maximumIntegerDigits = maximumIntegerDigits;
            this.maximumFractionDigits = maximumFractionDigits;
            this.minimumIntegerDigits = -1;
            this.minimumFractionDigits = -1;
        }

        public NumberFormatConfig(String pattern,
                            int scale,
                            RoundingMode roundingMode,
                            int minimumIntegerDigits,
                            int maximumIntegerDigits,
                            int maximumFractionDigits,
                            int minimumFractionDigits) {
            this.pattern = pattern;
            this.scale = scale;
            this.roundingMode = roundingMode;
            this.minimumIntegerDigits = minimumIntegerDigits;
            this.maximumIntegerDigits = maximumIntegerDigits;
            this.maximumFractionDigits = maximumFractionDigits;
            this.minimumFractionDigits = minimumFractionDigits;
        }


        public NumberFormat create() {
            DecimalFormat df = new DecimalFormat(pattern);
            config(df);
            return df;
        }

        public void config(NumberFormat numberFactory) {
            numberFactory.setRoundingMode(roundingMode);
            if (maximumFractionDigits > 0) {
                numberFactory.setMaximumFractionDigits(maximumFractionDigits);
            }
            if (minimumFractionDigits > 0) {
                numberFactory.setMinimumFractionDigits(minimumFractionDigits);
            }
            if (maximumFractionDigits < 0 && minimumFractionDigits < 0) {
                numberFactory.setMinimumFractionDigits(scale);
                numberFactory.setMaximumFractionDigits(scale);
            }
            if (maximumIntegerDigits > 0) {
                numberFactory.setMaximumIntegerDigits(maximumIntegerDigits);
            }
            if (minimumIntegerDigits > 0) {
                numberFactory.setMinimumIntegerDigits(minimumIntegerDigits);
            }
        }

    }
    
    public static class NullableNumberFormat extends NullableFormat implements Serializable {

        private static final long serialVersionUID = 8770260343737030870L;

        private final NumberFormatConfig nc;
        private final NumberFormat nf;

        public NullableNumberFormat(NumberFormatConfig NumberFormatConfig) {
            this.nc = NumberFormatConfig;
            this.nf = NumberFormatConfig.create();
        }

        @Override
        protected Format getFormat() {
            return this.nf;
        }

        /**
         * 将数字精确后再格式化为对应格式的数字文本
         */
        public String format(Number number) {
            if (number == null) {
                return null;
            }
            return nf.format(number);
        }

        /**
         * 通过初始化的配置信息精确小数
         * 
         * @see NumberFormatConfig
         */
        public Number scale(Number number) {
            if (number == null) {
                return null;
            }
            return BigDecimal.valueOf(number.doubleValue()).setScale(nc.scale, nc.roundingMode);
        }

        // 文本数字转化为数字

        public Number parse(String source, boolean scale) throws ParseException {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            return scale ? scale(parse(source)) : parse(source);
        }

        /**
         * 将数字文本转为数字并对数字精确后返回
         */
        public Number parse(String source) throws ParseException {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            return nf.parse(source);
        }

        public NumberFormatConfig getNumberFormatConfig() {
            return nc;
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
         * 格式化时间
         * 
         * @param date
         * @return
         * @see {@linkplain java.text.SimpleDateFormat#format(Date)}
         */
        public String format(Date date) {
            if (date == null) {
                return null;
            }
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
            if (c == null) {
                return null;
            }
            synchronized (sdf) {
                return sdf.format(c.getTime());
            }
        }

        /**
         * 解析时间
         * 
         * @param source
         *            文本时间格式
         * @return 解析后的时间
         * @throws ParseException
         * @see {@linkplain java.text.SimpleDateFormat#parse(String)}
         */
        public Date parseDate(String source) throws ParseException {
            if (StringUtils.isBlank(source)) {
                return null;
            }
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
            if (StringUtils.isBlank(source)) {
                return null;
            }
            Date date = parseDate(source);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c;
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

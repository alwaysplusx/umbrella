package com.harmony.umbrella.data.sql;

import static com.harmony.umbrella.util.Formats.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author wuxii@foxmail.com
 */
public abstract class SQLFormat {

    public static final String DATE_PATTERN = DEFAULT_DATE_PATTERN;

    private static final String PATTERN_KEY = "date.format.pattern";

    public static String format(String sql, FormatStyle style) {
        if (style == null) {
            style = FormatStyle.BASIC;
        }
        return style.getFormatter().format(sql);
    }

    public static String format(String sql) {
        return format(sql, FormatStyle.BASIC);
    }

    public static String sqlValue(Object value) {
        return sqlValue(value, new Properties());
    }

    public static String sqlValue(Object value, Properties props) {
        if (value == null) {
            return "''";

        } else if (value instanceof String || value instanceof Number) {
            return "'" + value + "'";

        } else if (value.getClass().isArray()) {

            if (value.getClass() == int[].class) {
                return sqlValue((int[]) value);

            } else if (value.getClass() == byte[].class) {
                return sqlValue((byte[]) value);

            } else if (value.getClass() == float[].class) {
                return sqlValue((float[]) value);

            } else if (value.getClass() == double[].class) {
                return sqlValue((double[]) value);

            } else if (value.getClass() == long[].class) {
                return sqlValue((long[]) value);

            } else if (value.getClass() == short[].class) {
                return sqlValue((short[]) value);

            } else if (value.getClass() == char[].class) {
                return sqlValue((char[]) value);

            }
            return sqlValue((Object[]) value);

        } else if (value instanceof Collection) {
            return sqlValue((Collection<?>) value);

        } else if (value instanceof Date) {
            return sqlValue((Date) value, props.getProperty(PATTERN_KEY, DATE_PATTERN));

        } else if (value instanceof Calendar) {
            return sqlValue((Calendar) value, props.getProperty(PATTERN_KEY, DATE_PATTERN));

        }
        return "'" + value + "'";

    }

    public static String sqlValue(Object[] value) {
        if (value instanceof String[]) {
            return sqlValue((String[]) value);

        } else if (value instanceof Integer[]) {
            return sqlValue((Integer[]) value);

        } else if (value instanceof Long[]) {
            return sqlValue((Long[]) value);

        } else if (value instanceof BigDecimal[]) {
            return sqlValue((BigDecimal[]) value);

        } else if (value instanceof Number[]) {
            return sqlValue((Number[]) value);

        } else if (value instanceof Short[]) {
            return sqlValue((Short[]) value);

        } else if (value instanceof BigInteger[]) {
            return sqlValue((BigInteger[]) value);

        } else if (value instanceof Float[]) {
            return sqlValue((Float[]) value);

        } else if (value instanceof Double[]) {
            return sqlValue((Double[]) value);

        } else {
            StringBuilder buf = new StringBuilder();
            for (Object b : value) {
                buf.append(", ").append(sqlValue(b));
            }
            return buf.substring(2);
        }
    }

    public static String sqlValue(Collection<?> value) {
        if (value == null || value.isEmpty()) {
            return "''";
        }
        StringBuilder buf = new StringBuilder();
        Iterator<?> it = value.iterator();
        while (it.hasNext()) {
            buf.append(", ").append(sqlValue(it.next()));
        }
        return buf.substring(2);
    }

    public static String sqlValue(Date value) {
        return sqlValue(value, DATE_PATTERN);
    }

    public static String sqlValue(Date value, String pattern) {
        if (value == null) {
            return "''";
        }
        return "'" + createDateFormat(pattern).format(value) + "'";
    }

    public static String sqlValue(Calendar value) {
        return sqlValue(value, DATE_PATTERN);
    }

    public static String sqlValue(Calendar value, String pattern) {
        if (value == null) {
            return "''";
        }
        return "'" + createDateFormat(pattern).format(value) + "'";
    }

    public static String sqlValue(String[] value) {
        if (value == null || value.length == 0) {
            return "''";
        }
        StringBuilder buf = new StringBuilder();
        for (String b : value) {
            buf.append(", '").append(b).append("'");
        }
        return buf.substring(2);
    }

    // ====================== number

    public static String sqlValue(Integer[] value) {
        return numberSqlValue(value);
    }

    public static String sqlValue(Long[] value) {
        return numberSqlValue(value);
    }

    public static String sqlValue(BigDecimal[] value) {
        return numberSqlValue(value);
    }

    public static String sqlValue(Number[] value) {
        return numberSqlValue(value);
    }

    public static String sqlValue(Short[] value) {
        return numberSqlValue(value);
    }

    public static String sqlValue(BigInteger[] value) {
        return numberSqlValue(value);
    }

    public static String sqlValue(Float[] value) {
        return numberSqlValue(value);
    }

    public static String sqlValue(Double[] value) {
        return numberSqlValue(value);
    }

    private static String numberSqlValue(Number[] value) {
        return numberSqlValue(value, '\'');
    }

    private static String numberSqlValue(Number[] value, char extension) {
        StringBuilder buf = new StringBuilder();
        for (Number b : value) {
            buf.append(", ").append(extension).append(b).append(extension);
        }
        return buf.substring(2);
    }

    public static String sqlValue(short[] value) {
        StringBuilder buf = new StringBuilder();
        for (short b : value) {
            buf.append(", '").append(b).append("'");
        }
        return buf.substring(2);
    }

    public static String sqlValue(long[] value) {
        StringBuilder buf = new StringBuilder();
        for (long b : value) {
            buf.append(", '").append(b).append("'");
        }
        return buf.substring(2);
    }

    public static String sqlValue(int[] value) {
        StringBuilder buf = new StringBuilder();
        for (int b : value) {
            buf.append(", '").append(b).append("'");
        }
        return buf.substring(2);
    }

    public static String sqlValue(float[] value) {
        StringBuilder buf = new StringBuilder();
        for (float b : value) {
            buf.append(", '").append(b).append("'");
        }
        return buf.substring(2);
    }

    public static String sqlValue(double[] value) {
        StringBuilder buf = new StringBuilder();
        for (double b : value) {
            buf.append(", '").append(b).append("'");
        }
        return buf.substring(2);
    }

    public static String sqlValue(byte[] value) {
        StringBuilder buf = new StringBuilder();
        for (byte b : value) {
            buf.append(", '").append(b).append("'");
        }
        return buf.substring(2);
    }

    public static String sqlValue(char[] value) {
        StringBuilder buf = new StringBuilder();
        for (char b : value) {
            buf.append(", '").append(b).append("'");
        }
        return buf.substring(2);
    }

}
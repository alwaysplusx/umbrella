package com.harmony.umbrella.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author wuxii@foxmail.com
 */
public class DigitUtils {

    public static boolean isDigit(String text) {
        if (text == null) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see BigDecimal#setScale(int, int)
     */
    public static Number scale(Number number, int scale, int roundingMode) {
        Assert.notNull(number);
        return scale(number, scale, RoundingMode.valueOf(roundingMode));
    }

    /**
     * @see BigDecimal#setScale(int, RoundingMode)
     */
    public static Number scale(Number number, int scale, RoundingMode roundingMode) {
        Assert.notNull(number);
        return Formats.createNumberFormat(new NumberConfig(scale, roundingMode)).scale(number);
    }

    /**
     * @see BigDecimal#round(MathContext)
     */
    public static Number precision(Number number, MathContext mathContext) {
        Assert.notNull(number);
        return new BigDecimal(number.doubleValue(), mathContext);
    }

    /**
     * @see NumberFormat#format(double)
     * @see DecimalFormat
     */
    public static String format(Number number, String pattern, RoundingMode roundingMode) {
        Assert.notNull(number);
        DecimalFormat df = new DecimalFormat(pattern);
        df.setRoundingMode(roundingMode);
        return df.format(number);
    }

}

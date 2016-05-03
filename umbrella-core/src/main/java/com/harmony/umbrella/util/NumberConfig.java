package com.harmony.umbrella.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author wuxii@foxmail.com
 */
public class NumberConfig {

    private static final String PATTERN = "#.##";

    public static final NumberConfig HALF_UP_2;

    public static final NumberConfig HALF_UP_4;

    public static final NumberConfig HALF_UP_6;

    static {
        HALF_UP_2 = new NumberConfig(PATTERN, 2, RoundingMode.HALF_UP);
        HALF_UP_4 = new NumberConfig(PATTERN, 4, RoundingMode.HALF_UP);
        HALF_UP_6 = new NumberConfig(PATTERN, 6, RoundingMode.HALF_UP);
    }

    public final String pattern;
    public final int scale;
    public final RoundingMode roundingMode;

    public final int minimumIntegerDigits;
    public final int maximumIntegerDigits;
    public final int maximumFractionDigits;
    public final int minimumFractionDigits;

    public NumberConfig(int scale, RoundingMode roundingMode) {
        this(PATTERN, scale, roundingMode, -1, -1, -1, -1);
    }

    public NumberConfig(String pattern, int scale, RoundingMode roundingMode) {
        this(pattern, scale, roundingMode, -1, -1, -1, -1);
    }

    public NumberConfig(String pattern, RoundingMode roundingMode, int maximumIntegerDigits, int maximumFractionDigits) {
        this.pattern = pattern;
        this.roundingMode = roundingMode;
        this.scale = maximumFractionDigits;
        this.maximumIntegerDigits = maximumIntegerDigits;
        this.maximumFractionDigits = maximumFractionDigits;
        this.minimumIntegerDigits = -1;
        this.minimumFractionDigits = -1;
    }

    public NumberConfig(String pattern,
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

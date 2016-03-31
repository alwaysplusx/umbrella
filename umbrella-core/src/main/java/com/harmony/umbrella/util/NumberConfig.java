/*
 * Copyright 2002-2014 the original author or authors.
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author wuxii@foxmail.com
 */
public class NumberConfig {

    public static final NumberConfig DEFAULT_CONFIG = new NumberConfig("#.##", 2, RoundingMode.HALF_UP);

    private static final String DEFAULT_PATTERN = "#.##";

    public final String pattern;
    public final int scale;
    public final RoundingMode roundingMode;

    public final int minimumIntegerDigits;
    public final int maximumIntegerDigits;
    public final int maximumFractionDigits;
    public final int minimumFractionDigits;

    public NumberConfig(int scale, RoundingMode roundingMode) {
        this(DEFAULT_PATTERN, scale, roundingMode, -1, -1, -1, -1);
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

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

import java.math.BigDecimal;

/**
 * @author wuxii@foxmail.com
 */
public class MathUtils {

    /**
     * 两数相加
     */
    public static BigDecimal add(Number a, Number b) {
        return sum(a, b);
    }

    public static BigDecimal add(Number a, Number b, MathConverter converter) {
        return sum(converter, a, b);
    }

    public static BigDecimal subtract(Number a, Number b) {
        return subtract(a, b, Original);
    }

    public static BigDecimal subtract(Number a, Number b, MathConverter converter) {
        return converter.convertLast(converter.convertResult(converter.coverterBefore(a).subtract(converter.coverterBefore(b))));
    }

    public static BigDecimal multiply(Number a, Number b) {
        return multiply(a, b, Original);
    }

    public static BigDecimal multiply(Number a, Number b, MathConverter converter) {
        return converter.convertLast(converter.convertResult(converter.coverterBefore(a).multiply(converter.coverterBefore(b))));
    }

    public static BigDecimal divide(Number a, Number b) {
        return divide(a, b, Original);
    }

    public static BigDecimal divide(Number a, Number b, MathConverter converter) {
        return converter.convertLast(converter.convertResult(converter.coverterBefore(a).divide(converter.coverterBefore(b))));
    }

    public static BigDecimal sum(Number... numbers) {
        return sum(Original, numbers);
    }

    public static BigDecimal sum(MathConverter converter, Number... numbers) {
        BigDecimal result = BigDecimal.ZERO;
        for (Number n : numbers) {
            result = converter.convertResult(result.add(converter.coverterBefore(n)));
        }
        return converter.convertLast(result);
    }

    private static final MathConverter Original = new MathConverter() {

        @Override
        public BigDecimal convert(Number t) {
            if (t instanceof BigDecimal) {
                return (BigDecimal) t;
            }
            return BigDecimal.valueOf(t.doubleValue());
        }

        @Override
        public BigDecimal coverterBefore(Number number) {
            return convert(number);
        }

        @Override
        public BigDecimal convertResult(Number number) {
            return convert(number);
        }

        @Override
        public BigDecimal convertLast(Number number) {
            return convert(number);
        }
    };
}

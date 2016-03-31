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
import java.math.RoundingMode;

/**
 * @author wuxii@foxmail.com
 */
public class MathUtils {

    /**
     * 两数相加
     */
    public static Number add(Number a, Number b) {
        return sum(a, b);
    }

    /**
     * 两数相加对得数进行精确
     * 
     * @see #add(Number, Number, NumberConfig)
     */
    public static Number add(Number a, Number b, int scale, RoundingMode roundingMode) {
        FormatMathConverter converter = new FormatMathConverter(new NumberConfig(scale, roundingMode), false, false, true);
        return sum(converter, a, b);
    }

    public static Number add(Number a, Number b, MathConverter converter) {
        return _sum(converter, a, b);
    }

    public static Number sum(Number... numbers) {
        return _sum(EmptyMathConverter, numbers);
    }

    public static Number sum(int scale, RoundingMode roundingMode, Number... numbers) {
        return null;
    }

    public static Number sum(MathConverter converter, Number... numbers) {
        return _sum(converter, numbers);
    }

    private static Number _sum(MathConverter converter, Number... numbers) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Number number : numbers) {
            sum = converter.convertResult(sum.add(converter.coverterBefore(number)));
        }
        return converter.convertLast(sum);
    }

    // FIXME 相关加减乘除

    public static void main(String[] args) {
        System.out.println(add(1.2123, 1.2312, 2, RoundingMode.HALF_UP));
    }

    private static final MathConverter EmptyMathConverter = new MathConverter() {

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

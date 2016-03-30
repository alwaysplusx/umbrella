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

import java.math.MathContext;
import java.math.RoundingMode;

/**
 * @author wuxii@foxmail.com
 */
public class MathUtils {

    public static final MathContext A;
    static {
        A = new MathContext(2, RoundingMode.HALF_UP);
    }

    public static Number add(Number a, Number b) {
        return add(a, b, MathContext.DECIMAL32);
    }

    public static Number add(Number a, Number b, MathContext mathContext) {
        return add(a, b, mathContext, false);
    }

    public static Number add(Number a, Number b, MathContext mathContext, boolean beforeCalculate) {
        Number x, y;
        if (beforeCalculate) {
            x = DigitUtils.precision(a, mathContext);
            y = DigitUtils.precision(b, mathContext);
        } else {
            x = a;
            y = b;
        }
        return DigitUtils.precision(x.doubleValue() + y.doubleValue(), mathContext);
    }

    public static Number sum(Number... numbers) {
        return null;
    }

    // FIXME 相关加减乘除

    public static void main(String[] args) {
        System.out.println(add(1.2123, 1.2312).doubleValue());
    }
}

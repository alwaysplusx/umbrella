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

    /**
     * 两数相加, 并提供数字在计算中的处理
     * 
     * @see MathConverter
     * @see BigDecimal#add(BigDecimal)
     */
    public static BigDecimal add(Number a, Number b, MathConverter converter) {
        return sum(converter, a, b);
    }

    /**
     * 两数相减
     */
    public static BigDecimal subtract(Number a, Number b) {
        return subtract(a, b, Original);
    }

    /**
     * 两数相减, 并提供数字在计算中的处理
     * 
     * @see MathConverter
     * @see BigDecimal#subtract(BigDecimal)
     */
    public static BigDecimal subtract(Number a, Number b, MathConverter converter) {
        return converter.convertLast(converter.convertResult(converter.covertBefore(a).subtract(converter.covertBefore(b))));
    }

    /**
     * 两数相乘
     */
    public static BigDecimal multiply(Number a, Number b) {
        return multiply(a, b, Original);
    }

    /**
     * 两数相乘, 并提供数字在计算中的处理
     * 
     * @see MathConverter
     * @see BigDecimal#multiply(BigDecimal)
     */
    public static BigDecimal multiply(Number a, Number b, MathConverter converter) {
        return converter.convertLast(converter.convertResult(converter.covertBefore(a).multiply(converter.covertBefore(b))));
    }

    /**
     * 两数相除
     */
    public static BigDecimal divide(Number a, Number b) {
        return divide(a, b, Original);
    }

    /**
     * 两数相除, 并提供数字在计算中的处理
     * 
     * @see MathConverter
     * @see BigDecimal#divide(BigDecimal)
     */
    public static BigDecimal divide(Number a, Number b, MathConverter converter) {
        return converter.convertLast(converter.convertResult(converter.covertBefore(a).divide(converter.covertBefore(b))));
    }

    /**
     * 多数求和
     */
    public static BigDecimal sum(Number... numbers) {
        return sum(Original, numbers);
    }

    /**
     * 多数求和
     * 
     * @see MathConverter
     * @see BigDecimal#add(BigDecimal)
     */
    public static BigDecimal sum(MathConverter converter, Number... numbers) {
        BigDecimal result = BigDecimal.ZERO;
        for (Number n : numbers) {
            result = converter.convertResult(result.add(converter.covertBefore(n)));
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
        public BigDecimal covertBefore(Number number) {
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

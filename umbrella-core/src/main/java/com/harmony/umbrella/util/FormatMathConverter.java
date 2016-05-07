package com.harmony.umbrella.util;

import java.math.BigDecimal;

/**
 * @author wuxii@foxmail.com
 */
public class FormatMathConverter implements MathConverter {

    private NumberConfig nc;

    private boolean before;

    private boolean last;

    private boolean result;

    public FormatMathConverter(NumberConfig nc, boolean before, boolean result, boolean last) {
        this.nc = nc;
        this.before = before;
        this.last = last;
        this.result = result;
    }

    @Override
    public BigDecimal convert(Number t) {
        return t instanceof BigDecimal ? (BigDecimal) t : BigDecimal.valueOf(t.doubleValue());
    }

    @Override
    public BigDecimal covertBefore(Number number) {
        BigDecimal num = convert(number);
        return before ? scale(num) : num;
    }

    @Override
    public BigDecimal convertResult(Number number) {
        BigDecimal num = convert(number);
        return result ? scale(num) : num;
    }

    @Override
    public BigDecimal convertLast(Number number) {
        BigDecimal num = convert(number);
        return last ? scale(num) : num;
    }

    private BigDecimal scale(BigDecimal num) {
        return num.setScale(nc.scale, nc.roundingMode);
    }
}

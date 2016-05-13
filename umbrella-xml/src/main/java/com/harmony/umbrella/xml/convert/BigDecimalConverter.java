package com.harmony.umbrella.xml.convert;

import java.math.BigDecimal;

/**
 * @author wuxii@foxmail.com
 */
public class BigDecimalConverter extends AbstractValueConverter<BigDecimal> {

    //private String pattern;
    //private RoundingMode mode;

    @Override
    protected void init() {
        // this.pattern = getAttribute("format");
        // this.mode = RoundingMode.valueOf(getAttribute("round"));
    }

    @Override
    protected BigDecimal convertValue(String t) {
        // String format = DigitUtils.format(new BigDecimal(t), pattern, mode);
        return new BigDecimal(t);
    }
}

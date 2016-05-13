package com.harmony.umbrella.xml.convert;

/**
 * @author wuxii@foxmail.com
 */
public final class DoubleConverter extends AbstractValueConverter<Double> {

    @Override
    protected void init() {
    }

    @Override
    protected Double convertValue(String t) {
        return Double.valueOf(t);
    }

}

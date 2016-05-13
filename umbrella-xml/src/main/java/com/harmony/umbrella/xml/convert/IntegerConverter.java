package com.harmony.umbrella.xml.convert;

/**
 * @author wuxii@foxmail.com
 */
public final class IntegerConverter extends AbstractValueConverter<Integer> {

    @Override
    protected void init() {
    }

    @Override
    protected Integer convertValue(String t) {
        return Integer.valueOf(t);
    }

}
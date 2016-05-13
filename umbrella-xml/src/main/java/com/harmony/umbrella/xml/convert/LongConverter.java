package com.harmony.umbrella.xml.convert;

/**
 * @author wuxii@foxmail.com
 */
public class LongConverter extends AbstractValueConverter<Long> {

    @Override
    protected void init() {
    }

    @Override
    protected Long convertValue(String t) {
        return Long.valueOf(t);
    }

}

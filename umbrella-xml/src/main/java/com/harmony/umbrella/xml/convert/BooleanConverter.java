package com.harmony.umbrella.xml.convert;

/**
 * @author wuxii@foxmail.com
 */
public class BooleanConverter extends AbstractValueConverter<Boolean> {

    @Override
    protected void init() {
    }

    @Override
    protected Boolean convertValue(String t) {
        return Boolean.valueOf(t);
    }

}

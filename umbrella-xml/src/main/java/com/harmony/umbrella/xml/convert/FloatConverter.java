package com.harmony.umbrella.xml.convert;

/**
 * @author wuxii@foxmail.com
 */
public class FloatConverter extends AbstractValueConverter<Float> {

    @Override
    protected void init() {
    }

    @Override
    protected Float convertValue(String t) {
        return Float.valueOf(t);
    }

}

package com.harmony.umbrella.xml.convert;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class EnumConverter extends AbstractValueConverter<Enum> {

    @Override
    protected void init() {

    }

    @Override
    @SuppressWarnings("unchecked")
    protected Enum convertValue(String t) {
        return Enum.valueOf((Class<? extends Enum>) valueType, t);
    }

}

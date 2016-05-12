package com.harmony.umbrella.xml;

import com.harmony.umbrella.util.Converter;

/**
 * @author wuxii@foxmail.com
 */
public class StringToStringConverter implements Converter<String, String> {

    @Override
    public String convert(String t) {
        return t;
    }

}

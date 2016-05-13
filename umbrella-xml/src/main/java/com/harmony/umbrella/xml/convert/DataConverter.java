package com.harmony.umbrella.xml.convert;

import java.text.ParseException;
import java.util.Date;

import com.harmony.umbrella.util.TimeUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
public final class DataConverter extends AbstractValueConverter<Date> {

    private String pattern;

    @Override
    protected void init() {
        this.pattern = getAttribute("format");
    }

    @Override
    protected Date convertValue(String t) {
        Date result = null;
        try {
            result = TimeUtils.toDate(t, pattern);
        } catch (ParseException e) {
        }
        return result;
    }

}
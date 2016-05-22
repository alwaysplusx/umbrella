package com.harmony.umbrella.xml.convert;

import java.text.ParseException;
import java.util.Date;

import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.util.TimeUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
public final class DateConverter extends AbstractValueConverter<Date> {

    private String pattern;

    @Override
    protected void init() {
        this.pattern = getAttribute("format");
    }

    @Override
    protected Date convertValue(String t) {
        if (StringUtils.isNotBlank(pattern)) {
            try {
                return TimeUtils.toDate(t, pattern);
            } catch (ParseException e) {
                ReflectionUtils.rethrowRuntimeException(e);
            }
        }
        try {
            return TimeUtils.toDate(t);
        } catch (ParseException e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }
        throw new IllegalArgumentException();
    }

}
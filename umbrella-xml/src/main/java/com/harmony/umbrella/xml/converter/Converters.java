/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.xml.converter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.util.Converter;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.TimeUtils;

/**
 * @author wuxii@foxmail.com
 */
public class Converters {

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);

    /**
     * Map with primitive type as key and corresponding wrapper type as value,
     * for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return primitiveTypeToWrapperMap.containsKey(clazz);
    }

    public static final class StringToDateConverter implements Converter<String, Date> {

        private String pattern;

        public StringToDateConverter() {
            this("yyyy-MM-dd HH:mm:ss");
        }

        public StringToDateConverter(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public Date convert(String t) {
            try {
                return TimeUtils.toDate(t, pattern);
            } catch (ParseException e) {
                ReflectionUtils.rethrowRuntimeException(e);
                throw new IllegalArgumentException(e);
            }
        }

    }

    public static final class StringToCalendarConverter implements Converter<String, Calendar> {

        private String pattern;

        public StringToCalendarConverter() {
            this("yyyy-MM-dd HH:mm:ss");
        }

        public StringToCalendarConverter(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public Calendar convert(String t) {
            try {
                return TimeUtils.toCalendar(t, pattern);
            } catch (ParseException e) {
                ReflectionUtils.rethrowRuntimeException(e);
                throw new IllegalArgumentException(e);
            }
        }

    }

    public static final class StringToIntegerConverter implements Converter<String, Integer> {

        @Override
        public Integer convert(String t) {
            return Integer.valueOf(t);
        }

    }

    public static final class StringToDoubleConverter implements Converter<String, Double> {

        @Override
        public Double convert(String t) {
            return Double.valueOf(t);
        }

    }

    public static final class StringToLongConverter implements Converter<String, Long> {

        @Override
        public Long convert(String t) {
            return Long.valueOf(t);
        }

    }

    public static final class StringToFloatConverter implements Converter<String, Float> {

        @Override
        public Float convert(String t) {
            return Float.valueOf(t);
        }

    }

    public static final class StringToBooleanConverter implements Converter<String, Boolean> {

        @Override
        public Boolean convert(String t) {
            return Boolean.valueOf(t);
        }

    }

}

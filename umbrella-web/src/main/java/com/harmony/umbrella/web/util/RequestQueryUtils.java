/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.web.util;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.Bond.Link;
import com.harmony.umbrella.data.bond.JunctionBond.Operator;

/**
 * @author wuxii@foxmail.com
 */
public class RequestQueryUtils {

    private static final Map<String, Link> linkMap = new HashMap<String, Link>();
    private static final Map<String, Operator> operatorMap = new HashMap<String, Operator>();

    static {

        for (Link link : Link.values()) {
            linkMap.put(link.shortName(), link);
        }

        for (Operator op : Operator.values()) {
            operatorMap.put(op.shortName(), op);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(RequestQueryUtils.class);

    public static Bond filterRequest(String filterPrefix, String filterSplit, Class<?> modelType, HttpServletRequest request) {
        String filterName = filterPrefix + filterSplit;
        Enumeration<String> names = request.getParameterNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.startsWith(filterName)) {
                if (name.split(filterPrefix).length < 3) {
                    log.warn("http request parameter name {} start with {}, but split with {} not enough", name, filterName, filterSplit);
                    continue;
                }
                FilterParameter param = new FilterParameter(name, filterPrefix, filterSplit, modelType);
            }
        }
        return null;
    }

    public static Bond filterParameter(String name, Class<?> modelType, Object value) {

        return null;
    }

    private static Class<?> parameterType(Class<?> modelType, String parameterName) {
        Class<?> parameterType = null;
        int pointIndex = parameterName.indexOf(".");
        try {
            if (pointIndex > 0) {
                String name = parameterName.substring(0, pointIndex);
                Field field = modelType.getDeclaredField(name);
                parameterType = parameterType(field.getType(), parameterName.substring(pointIndex));
            } else {
                return modelType.getDeclaredField(parameterName).getType();
            }
        } catch (NoSuchFieldException e) {
            return null;
        }
        return parameterType;
    }

    /**
     * f_o_eq_name_xx
     * <p>
     * f_eq_name_xx
     * 
     * @author wuxii@foxmail.com
     */
    static class FilterParameter {

        private final Class<?> modelType;
        private final String originalName;
        private final String prefix;
        private final String split;

        private Link link;
        private Operator operator;
        private String parameterName;
        private Class<?> classType;
        private Object value;

        public FilterParameter(String originalName, String prefix, String split, Class<?> modelType) {
            this.originalName = originalName;
            this.modelType = modelType;
            this.prefix = prefix;
            this.split = split;
            this.readProperties(originalName.split(split));
        }

        private void readProperties(String[] names) {
            String name_1 = names[1];
            String name_2 = names[2];

            if (operatorMap.containsKey(name_1)) {
                this.operator = operatorMap.get(name_1);
            } else {
                this.operator = Operator.AND;
                if (linkMap.containsKey(name_1)) {
                    this.link = linkMap.get(name_1);
                }
            }

            if (link == null && linkMap.containsKey(name_2)) {
                this.link = linkMap.get(name_2);
            }

            if (link == null) {
                throw new IllegalArgumentException(originalName + " link parameter is wrong");
            }

            int linkIndex = originalName.indexOf(link.shortName()) + link.shortName().length() + split.length();

            this.parameterName = originalName.substring(linkIndex, originalName.length()).replace(split, ".");

            this.classType = parameterType(modelType, parameterName);

        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{\"modelType\":\"");
            builder.append(modelType);
            builder.append("\", \"originalName\":\"");
            builder.append(originalName);
            builder.append("\", \"prefix\":\"");
            builder.append(prefix);
            builder.append("\", \"split\":\"");
            builder.append(split);
            builder.append("\", \"link\":\"");
            builder.append(link);
            builder.append("\", \"operator\":\"");
            builder.append(operator);
            builder.append("\", \"parameterName\":\"");
            builder.append(parameterName);
            builder.append("\", \"classType\":\"");
            builder.append(classType);
            builder.append("\", \"value\":\"");
            builder.append(value);
            builder.append("\"}");
            return builder.toString();
        }

    }

    public static void main(String[] args) {

        System.out.println(new FilterParameter("f_a_eq_name", "f", "_", Object.class));

        System.out.println(new FilterParameter("f_lk_user_age", "f", "_", Object.class));

    }
}

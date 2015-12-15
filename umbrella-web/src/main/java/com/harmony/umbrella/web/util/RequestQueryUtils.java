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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.Bond.Link;
import com.harmony.umbrella.data.bond.BondBuilder;
import com.harmony.umbrella.data.bond.JunctionBond.Operator;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class RequestQueryUtils {

    //private static final Logger log = LoggerFactory.getLogger(RequestQueryUtils.class);

    private static final Map<String, Link> linkMap = new HashMap<String, Link>();
    private static final Map<String, Operator> operatorMap = new HashMap<String, Operator>();

    private static String filter;
    private static String split;

    static {

        /*ApplicationContext applicationContext = ApplicationContext.getApplicationContext();

        String linkClassName = Link.class.getName();
        for (Link link : Link.values()) {
            String key = applicationContext.getProperty(linkClassName + link.name(), link.shortName());
            linkMap.put(key.toLowerCase(), link);
        }

        String opClassName = Operator.class.getName();
        for (Operator op : Operator.values()) {
            String key = applicationContext.getProperty(opClassName + op.name(), op.shortName());
            operatorMap.put(key.toLowerCase(), op);
        }

        filter = applicationContext.getProperty("application.web.front.filter", "f");
        split = applicationContext.getProperty("application.web.front.split", "_");*/

    }

    public static Bond toBond(FilterParameter... filterParameters) {
        BondBuilder builder = BondBuilder.newInstance();
        Bond result = null;

        for (FilterParameter fp : filterParameters) {
            Bond bond = null;
            switch (fp.link) {
            case EQUAL:
                bond = builder.equal(fp.parameterName, fp.value);
                break;
            case GREATER_THAN:
                bond = builder.gt(fp.parameterName, fp.value);
                break;
            case GREATER_THAN_OR_EQUAL:
                bond = builder.ge(fp.parameterName, fp.value);
                break;
            case IN:
                bond = builder.in(fp.parameterName, fp.value);
                break;
            case LESS_THAN:
                bond = builder.lt(fp.parameterName, fp.value);
                break;
            case LESS_THAN_OR_EQUAL:
                bond = builder.le(fp.parameterName, fp.value);
                break;
            case LIKE:
                bond = builder.like(fp.parameterName, (String) fp.value);
                break;
            case NOT_EQUAL:
                bond = builder.notEqual(fp.parameterName, fp.value);
                break;
            case NOT_IN:
                bond = builder.notIn(fp.parameterName, fp.value);
                break;
            case NOT_LIKE:
                bond = builder.notLike(fp.parameterName, (String) fp.value);
                break;
            case NOT_NULL:
                bond = builder.isNotNull(fp.parameterName);
                break;
            case NULL:
                bond = builder.isNull(fp.parameterName);
                break;
            default:
                throw new IllegalArgumentException("request is illegal link " + fp.link);
            }

            if (result == null) {
                result = bond;
            } else if (Operator.AND == fp.operator) {
                result = builder.and(result, bond);
            } else {
                result = builder.or(result, bond);
            }
        }

        return result == null ? BondBuilder.yes() : result;
    }

    public static FilterParameter[] filterRequest(Class<?> modelType, HttpServletRequest request) {
        return filterRequest(modelType, request, true);
    }

    public static FilterParameter[] filterRequest(Class<?> modelType, HttpServletRequest request, boolean ignoreBlankValue) {
        List<FilterParameter> result = new ArrayList<FilterParameter>();

        final String prefix = RequestQueryUtils.filter;
        final String split = RequestQueryUtils.split;
        final String filterPrefix = prefix + split;

        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String[] values = request.getParameterValues(name);
            if (name.startsWith(filterPrefix) && name.split(split).length > 2 && (ignoreBlankValue || !isBlankArray(values))) {
                FilterParameter fp = new FilterParameter(name, prefix, split, modelType);
                applyValue(fp, values);
                result.add(fp);
            }
        }

        return result.toArray(new FilterParameter[result.size()]);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void applyValue(FilterParameter fp, String[] values) {
        if (values.length > 1) {
            List valueList = new ArrayList();
            for (String value : values) {
                valueList.add(convert(fp.javaType, value.trim()));
            }
        } else {
            fp.value = convert(fp.javaType, values[0].trim());
        }
    }

    private static Object convert(Class<?> requireType, String value) {
        return value;
    }

    private static boolean isBlankArray(String[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        for (String str : array) {
            if (StringUtils.isNotBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查询请求参数对应的java类型
     */
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
    public final static class FilterParameter {

        // 查询的对象 
        private final Class<?> modelType;
        // request中的请求名称
        private final String originalName;
        private final String prefix;
        private final String split;

        //and or
        private Link link;
        // equal and so on
        private Operator operator;
        // 需要查询的字段
        private String parameterName;
        // 查询的字段java类型
        private Class<?> javaType;
        private Object value;

        public FilterParameter(String originalName, String prefix, String split, Class<?> modelType) {
            Assert.isTrue(originalName.split(split).length > 2, originalName + " not a request parameter");
            Assert.isTrue(originalName.startsWith(prefix), originalName + " not start with " + prefix);
            this.originalName = originalName;
            this.prefix = prefix;
            this.split = split;
            this.modelType = modelType;
            this.readProperties(originalName.split(split));
        }

        /**
         * e.g.: f_a_eq_name, f_eq_name, f_eq_user_name
         */
        private void readProperties(String[] names) {

            int parameterNameIndex = 0;

            String name1 = names[1];// may be a or eq
            String name2 = names[2];// may be eq or name

            String name1LowerCase = names[1].toLowerCase();
            String name2LowerCase = names[2].toLowerCase();

            if (operatorMap.containsKey(name1LowerCase)) {
                this.operator = operatorMap.get(name1LowerCase);
            } else {
                this.operator = Operator.AND;
                if (linkMap.containsKey(name1LowerCase)) {
                    this.link = linkMap.get(name1LowerCase);
                    parameterNameIndex = originalName.indexOf(name1) + name1.length() + split.length();
                }
            }

            if (link == null && linkMap.containsKey(name2LowerCase)) {
                this.link = linkMap.get(name2LowerCase);
                parameterNameIndex = originalName.indexOf(name2) + name2.length() + split.length();
            }

            if (link == null) {
                throw new IllegalArgumentException(originalName + " link parameter is wrong");
            }

            if (originalName.length() <= parameterNameIndex) {
                throw new IllegalArgumentException(originalName + " not contains any parameter name");
            }

            this.parameterName = originalName.substring(parameterNameIndex).replace(split, ".");

            this.javaType = parameterType(modelType, parameterName);

        }

        public Class<?> getModelType() {
            return modelType;
        }

        public String getOriginalName() {
            return originalName;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSplit() {
            return split;
        }

        public Link getLink() {
            return link;
        }

        public Operator getOperator() {
            return operator;
        }

        public String getParameterName() {
            return parameterName;
        }

        public Class<?> getJavaType() {
            return javaType;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{link:");
            builder.append(link);
            builder.append(", operator:");
            builder.append(operator);
            builder.append(", parameterName:");
            builder.append(parameterName);
            builder.append("}");
            return builder.toString();
        }

    }

}

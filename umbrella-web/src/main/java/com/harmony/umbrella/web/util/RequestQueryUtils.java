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

    /**
     * f_o_eq_name_xx
     * <p>
     * f_eq_name_xx
     * 
     * @author wuxii@foxmail.com
     */
    public static class FilterParameter {

        private final Class<?> modelType;
        private final String originalName;
        private final String prefix;
        private final String split;

        private Link link;
        private Operator operator;
        private String parameterName;
        private Class<?> classType;

        public FilterParameter(String originalName, String prefix, String split, Class<?> modelType) {
            this.originalName = originalName;
            this.modelType = modelType;
            this.prefix = prefix;
            this.split = split;
            this.readProperties(originalName.split(prefix));
        }

        private void readProperties(String[] names) {
            String name_1 = names[1];
            String name_2 = names[2];

            if (operatorMap.containsKey(name_1)) {
                this.operator = operatorMap.get(name_1);
            }

            if (operator == null && linkMap.containsKey(name_1)) {
                this.link = linkMap.get(name_1);
            } else if (linkMap.containsKey(name_2)) {
                this.link = linkMap.get(name_2);
            }

            this.parameterName = names[1];

            for (int index = 1; index < names.length; index++) {
            }
            boolean firstPicked = false;
            boolean secondPicked = false;

            for (int index = 1; index < names.length; index++) {

                String name = names[index];

                if (!firstPicked) {
                    for (Operator op : Operator.values()) {
                        if (op.shortName().equals(name)) {
                            this.operator = op;
                            firstPicked = true;
                            break;
                        }
                    }
                }

                if (index > 1 && operator == null) {
                    this.operator = Operator.AND;
                }

                if (!firstPicked || !secondPicked) {
                    for (Link link : Link.values()) {
                        if (link.shortName().equals(name)) {
                            this.link = link;
                            firstPicked = true;
                            secondPicked = true;
                            break;
                        }
                    }
                }

            }

            if (operator == null) {
                this.operator = Operator.AND;
            }

        }
    }

}

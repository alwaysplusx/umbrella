/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.log.expression;

import java.util.ArrayList;
import java.util.Collection;

import com.harmony.umbrella.log.IllegalExpressionException;

/**
 * 支持对数组对象的取值，表达式为单个数字
 * 
 * @author wuxii@foxmail.com
 */
public class NumberExpressionResolver extends ComparableExpressionResolver {

    public static final NumberExpressionResolver INSTANCE = new NumberExpressionResolver();

    public NumberExpressionResolver() {
        super(NUMBER);
    }

    @Override
    public boolean support(String expression, Object value) {
        return support(expression) && valueIsArray(value);
    }

    public boolean support(String expression) {
        for (int i = 0, max = expression.length(); i < max; i++) {
            if (!Character.isDigit(expression.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean valueIsArray(Object value) {
        if (value == null) {
            return false;
        }
        Class<?> valueClass = value.getClass();
        if (valueClass.isArray()) {
            return true;
        }
        return value instanceof Collection;
    }

    @Override
    public Object resolve(String expression, Object value) throws IllegalExpressionException {
        if (support(expression)) {
            if (valueIsArray(value)) {
                Class<?> valueClass = value.getClass();
                int index = Integer.parseInt(expression);
                if (valueClass.isArray()) {
                    return getValueInArray(index, value);
                } else if (value instanceof Collection) {
                    return getValueInCollection(index, value);
                }
            }
            throw new IllegalExpressionException("illegal number value " + value);
        }
        throw new IllegalExpressionException("illegal number expression " + expression);
    }

    private Object getValueInArray(int index, Object value) {
        Object result = null;
        if (value instanceof Object[]) {
            result = ((Object[]) value)[index];
        } else if (value instanceof String[]) {
            result = ((String[]) value)[index];
        } else if (value instanceof Integer[]) {
            result = ((Integer[]) value)[index];
        } else if (value instanceof Long[]) {
            result = ((Long[]) value)[index];
        } else if (value instanceof long[]) {
            result = ((long[]) value)[index];
        } else if (value instanceof int[]) {
            result = ((int[]) value)[index];
        } else {
            throw new IllegalArgumentException("unsupported array type " + value.getClass().getName());
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getValueInCollection(int index, Object value) {
        return new ArrayList((Collection) value).get(index);
    }
}

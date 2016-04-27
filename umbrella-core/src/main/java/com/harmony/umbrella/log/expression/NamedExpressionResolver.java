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

import java.lang.reflect.Method;

import com.harmony.umbrella.log.IllegalExpressionException;
import com.harmony.umbrella.log.util.ExpressionUtils;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 表达式为单词，由字母构成。通过放射方式获取value中对应名称的值
 *
 * @author wuxii@foxmail.com
 */
public class NamedExpressionResolver extends ComparableExpressionResolver {

    protected static final NamedExpressionResolver INSTANCE = new NamedExpressionResolver();

    public NamedExpressionResolver() {
        super(NAMED);
    }

    @Override
    public boolean support(String expression, Object value) {
        return ExpressionUtils.isNamedExpression(expression);
    }

    @Override
    public Object resolve(String expression, Object value) {
        if (support(expression, value)) {
            try {
                // access method
                Method method = ReflectionUtils.findReadMethod(value.getClass(), expression);
                return ReflectionUtils.invokeMethod(method, value);
            } catch (Exception e) {
                try {
                    // access field
                    return ReflectionUtils.getFieldValue(expression, value);
                } catch (Exception e1) {
                    throw new IllegalExpressionException("unsupported named expression " + expression, e);
                }
            }
        }
        throw new IllegalExpressionException("illegal named expression " + expression);
    }

}

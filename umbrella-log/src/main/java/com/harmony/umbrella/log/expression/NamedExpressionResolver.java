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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.harmony.umbrella.log.IllegalExpressionException;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.util.ExpressionUtils;

/**
 * 表达式为单词，由字母构成。通过放射方式获取value中对应名称的值
 *
 * @author wuxii@foxmail.com
 */
public class NamedExpressionResolver extends ComparableExpressionResolver {

    private static final Log log = Logs.getLog(NamedExpressionResolver.class);

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
            Class<?> targetClass = value.getClass();
            try {
                // access method
                Method method = targetClass.getDeclaredMethod(readMethodName(expression));
                return method.invoke(value);
            } catch (Exception e) {
                try {
                    // access field
                    Field field = targetClass.getDeclaredField(expression);
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    return field.get(value);
                } catch (Exception e1) {
                    log.error(e);
                }
                throw new IllegalExpressionException("unsupported named expression " + expression, e);
            }
        }
        throw new IllegalExpressionException("illegal named expression " + expression);
    }

    protected static final String readMethodName(String name) {
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}

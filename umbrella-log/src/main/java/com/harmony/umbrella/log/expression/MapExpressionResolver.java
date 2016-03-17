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

import java.util.Map;

import com.harmony.umbrella.log.IllegalExpressionException;
import com.harmony.umbrella.log.util.ExpressionUtils;

/**
 * map表达式解析
 * <p>
 * 典型的数组表达式如：{@code name[key]} 数组表达式由两部分组成
 * <li>1.名称部分 - 'name'</li>
 * <li>2.数字部分 - '[key]'</li>
 * <p>
 * 取值分为两步骤，先将名称部分对应的值从value中取出，如果取出的值为map类型再取数组对应的key值
 *
 * @author wuxii@foxmail.com
 * @see ArrayExpressionResolver
 */
public class MapExpressionResolver extends ComparableExpressionResolver {

    protected static final MapExpressionResolver INSTANCE = new MapExpressionResolver();

    public MapExpressionResolver() {
        super(COMPLEX);
    }

    @Override
    public boolean support(String expression, Object value) {
        return support(expression);
    }

    public boolean support(String expression) {
        return ExpressionUtils.isMapExpression(expression);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object resolve(String expression, Object value) {
        if (support(expression)) {
            // step 1: 获取名称部分值
            String named = ExpressionUtils.getNamedPart(expression);
            value = NamedExpressionResolver.INSTANCE.resolve(named, value);
            if (value == null) {
                return null;
            }
            if (value instanceof Map) {
                // step 2: 获取map中的值
                String key = ExpressionUtils.getMapExpressionKey(expression);
                return ((Map) value).get(key);
            }
            throw new IllegalExpressionException("illegal map value " + value);
        }
        throw new IllegalExpressionException("illegal map expression " + expression);
    }
}

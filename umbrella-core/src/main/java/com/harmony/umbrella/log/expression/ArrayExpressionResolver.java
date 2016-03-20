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

import com.harmony.umbrella.log.IllegalExpressionException;
import com.harmony.umbrella.log.util.ExpressionUtils;

/**
 * 支持数组表达式取值
 * <p>
 * 典型的数组表达式如：{@code name[index]} 数组表达式由两部分组成
 * <li>1.名称部分 - 'name'</li>
 * <li>2.数字部分 - '[index]'</li>
 * <p>
 * 取值分为两步骤，先将名称部分对应的值从value中取出，如果取出的值为数组类型再取数组对应的index值
 *
 * @author wuxii@foxmail.com
 */
public class ArrayExpressionResolver extends ComparableExpressionResolver {

    protected static final ArrayExpressionResolver INSTANCE = new ArrayExpressionResolver();

    public ArrayExpressionResolver() {
        super(COMPLEX);
    }

    @Override
    public boolean support(String expression, Object value) {
        return support(expression);
    }

    public boolean support(String expression) {
        return ExpressionUtils.isArrayExpression(expression);
    }

    @Override
    public Object resolve(String expression, Object value) {
        if (support(expression)) {
            // step 1. 获取名称部分对应的值
            String named = ExpressionUtils.getNamedPart(expression);
            value = NamedExpressionResolver.INSTANCE.resolve(named, value);
            // step 2. 获取数组部分值
            if (value != null) {
                int index = ExpressionUtils.getArrayExpressionIndex(expression);
                value = NumberExpressionResolver.INSTANCE.resolve(index + "", value);
            }
            return value;
        }
        throw new IllegalExpressionException("illegal array expression " + expression);
    }

}

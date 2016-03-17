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

/**
 * 表达式的解析工具
 *
 * @author wuxii@foxmail.com
 */
public interface ExpressionResolver extends Comparable<ExpressionResolver> {

    // 优先级越大数字越大
    int LAST = Integer.MIN_VALUE;

    int NAMED = 1000;

    int NUMBER = 2000;

    int METHOD = 3000;

    int COMPLEX = 4000;

    int HTTP = 5000;

    int CUSTOM = 6000;

    int FIRST = Integer.MAX_VALUE;

    /**
     * 解析工具处理的优先级，数字越大表示的优先级越高
     */
    int priority();

    /**
     * 由解析工具自己决定符合条件的表达式
     *
     * @param expression
     *            表达式
     * @param value
     *            表达式需要解决的对象
     * @return true标识支持解析
     */
    boolean support(String expression, Object value);

    /**
     * 解析表达式从value中得出表达式所对应的值
     *
     * @param expression
     *            需要解析的表达式
     * @param value
     *            表达式对应的对象
     * @return 从对象中得出的表达式结果
     * @throws IllegalExpressionException
     *             - 如果表达式不符合{@linkplain #support(String, Object)}
     */
    Object resolve(String expression, Object value) throws IllegalExpressionException;

}

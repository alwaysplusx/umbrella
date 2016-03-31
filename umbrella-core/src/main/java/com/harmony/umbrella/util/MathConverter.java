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
package com.harmony.umbrella.util;

import java.math.BigDecimal;

/**
 * 数字计算的时候的帮助工具
 * 
 * @author wuxii@foxmail.com
 */
public interface MathConverter extends Converter<Number, BigDecimal> {

    /**
     * 计算前对数字处理, 如:可以将数字精确
     * 
     * @param number
     *            待处理的数字
     * @return 处理后的数字
     */
    BigDecimal coverterBefore(Number number);

    /**
     * 对计算后的得数进行处理.
     * 
     * @param number
     *            待处理的数字
     * @return 处理后的数字
     */
    BigDecimal convertResult(Number number);

    /**
     * 对数字计算的最后的数进行处理.
     * 
     * @param number
     *            待处理的数字
     * @return 处理后的数字
     */
    BigDecimal convertLast(Number number);

}

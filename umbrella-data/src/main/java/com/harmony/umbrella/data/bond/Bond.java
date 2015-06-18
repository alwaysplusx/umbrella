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
package com.harmony.umbrella.data.bond;

import java.io.Serializable;

/**
 * 基础查询条件Bond
 * 
 * @author wuxii@foxmail.com
 */
public interface Bond extends Serializable {

    String EQUAL = "=";
    String NOT_EQAUL = "<>";
    String IN = "in";
    String NOT_IN = "not in";
    String NULL = "is null";
    String NOT_NULL = "is not null";
    String LIKE = "like";
    String NOT_LIKE = "not like";
    String GREATER_EQUAL = ">=";
    String GREATER_THAN = ">";
    String LESS_EQUAL = "<=";
    String LESS_THAN = "<";
    String BETWEEN = "between";

    /**
     * 待查询的字段名称
     * 
     * @return 字段名称
     */
    String getName();

    /**
     * 待查询字段对应的条件值
     * 
     * @return 字段对应值
     */
    Object getValue();

    /**
     * 查询的逻辑关系：=, >, < 等等
     * 
     * @return 查询逻辑
     */
    String getLink();

    /**
     * 为待查询字段生成的别名
     * 
     * @return 字段别名
     */
    String getAlias();

    /**
     * 设置字段别名
     * 
     * @param alias
     *            字段别名
     */
    void setAlias(String alias);

    /**
     * 如果是{@code true}则表示{@linkplain #getName()}, {@linkplain #getValue()}
     * 为两个数据库中的字段比较
     * <p>
     * {@code false}则表示字段{@linkplain #getName()} 对应的数据库值与
     * {@linkplain #getValue()} 传入值比较
     * 
     * @return
     */
    boolean isInline();

}
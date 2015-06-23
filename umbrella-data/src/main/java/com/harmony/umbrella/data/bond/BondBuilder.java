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

import static com.harmony.umbrella.data.bond.Bond.Link.*;

import java.util.Arrays;
import java.util.Collection;

import com.harmony.umbrella.data.bond.Bond.Link;
import com.harmony.umbrella.data.bond.JunctionBond.Operator;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.domain.Sort.Direction;

/**
 * {@linkplain Bond}创建工具类
 * 
 * @author wuxii@foxmail.com
 */
public class BondBuilder {

    public static BondBuilder newInstance() {
        return new BondBuilder();
    }

    /**
     * 创建{@linkplain Link#EQUAL EQUAL}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond equal(String name, Object value) {
        beforeBondCreate(name, value, EQUAL, false);
        return afterBondCreated(new ComparisonBond(name, value, EQUAL));
    }

    /**
     * 创建{@linkplain Link#NOT_EQUAL NOT_EQUAL}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond notEqual(String name, Object value) {
        beforeBondCreate(name, value, NOT_EQUAL, false);
        return afterBondCreated(new ComparisonBond(name, value, NOT_EQUAL));
    }

    /**
     * 创建{@linkplain Link#IN IN}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond in(String name, Collection<?> c) {
        beforeBondCreate(name, c, IN, false);
        return afterBondCreated(new InBond(name, c));
    }

    /**
     * 创建{@linkplain Link#IN IN}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond in(String name, Object... values) {
        beforeBondCreate(name, values, IN, false);
        return afterBondCreated(new InBond(name, Arrays.asList(values)));
    }

    /**
     * 创建{@linkplain Link#NOT_IN NOT_IN}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond notIn(String name, Collection<?> c) {
        beforeBondCreate(name, c, NOT_IN, false);
        return afterBondCreated(new InBond(name, c, NOT_IN));
    }

    /**
     * 创建{@linkplain Link#NOT_IN NOT_IN}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond notIn(String name, Object... values) {
        beforeBondCreate(name, values, NOT_IN, false);
        return afterBondCreated(new InBond(name, Arrays.asList(values), NOT_IN));
    }

    /**
     * 创建{@linkplain Link#NULL NULL}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @return {@linkplain Bond}
     */
    public Bond isNull(String name) {
        beforeBondCreate(name, null, NULL, false);
        return afterBondCreated(new NullBond(name));
    }

    /**
     * 创建{@linkplain Link#NOT_NULL NOT_NULL}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @return {@linkplain Bond}
     */
    public Bond isNotNull(String name) {
        beforeBondCreate(name, null, NOT_NULL, false);
        return afterBondCreated(new NullBond(name, NOT_NULL));
    }

    /**
     * 创建{@linkplain Link#LIKE LIKE}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond like(String name, String value) {
        beforeBondCreate(name, value, LIKE, false);
        return afterBondCreated(new ComparisonBond(name, value, LIKE));
    }

    /**
     * 创建{@linkplain Link#NOT_LIKE NOT_LIKE}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond notLike(String name, String value) {
        beforeBondCreate(name, value, NOT_LIKE, false);
        return afterBondCreated(new ComparisonBond(name, value, NOT_LIKE));
    }

    /**
     * 创建{@linkplain Link#GREATER_THAN_OR_EQUAL GREATER_THAN_OR_EQUAL}条件的
     * {@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond ge(String name, Object value) {
        beforeBondCreate(name, value, GREATER_THAN_OR_EQUAL, false);
        return afterBondCreated(new ComparisonBond(name, value, GREATER_THAN_OR_EQUAL));
    }

    /**
     * 创建{@linkplain Link#GREATER_THAN GREATER_THAN}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond gt(String name, Object value) {
        beforeBondCreate(name, value, GREATER_THAN, false);
        return afterBondCreated(new ComparisonBond(name, value, GREATER_THAN));
    }

    /**
     * 创建{@linkplain Link#LESS_THAN_OR_EQUAL LESS_THAN_OR_EQUAL}条件的
     * {@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond le(String name, Object value) {
        beforeBondCreate(name, value, LESS_THAN_OR_EQUAL, false);
        return afterBondCreated(new ComparisonBond(name, value, LESS_THAN_OR_EQUAL));
    }

    /**
     * 创建{@linkplain Link#LESS_THAN LESS_THAN}条件的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    public Bond lt(String name, Object value) {
        beforeBondCreate(name, value, LESS_THAN, false);
        return afterBondCreated(new ComparisonBond(name, value, LESS_THAN));
    }

    /**
     * 创建内部比较的{@linkplain Bond}
     * 
     * @param name
     *            名称
     * @param expression
     *            比较的右边字段名称
     * @param link
     *            对比的关系
     * @return {@linkplain Bond}
     */
    public Bond inline(String name, String expression, Link link) {
        beforeBondCreate(name, expression, link, true);
        return afterBondCreated(new ComparisonBond(name, expression, link, true));
    }

    /**
     * 创建{@linkplain Bond}前被调用
     */
    protected void beforeBondCreate(String name, Object value, Link link, boolean isInline) {

    }

    /**
     * 创建{@linkplain Bond}后被调用
     * 
     */
    protected AbstractBond afterBondCreated(AbstractBond bond) {
        return bond;
    }

    /**
     * 将字段{@code name}按{@linkplain Direction#ASC}排序
     * 
     * @param name
     *            排序的字段
     * @return {@linkplain Sort}
     */
    public Sort asc(String... name) {
        return new Sort(Direction.ASC, name);
    }

    /**
     * 将字段{@code name}按{@linkplain Direction#DESC}排序
     * 
     * @param name
     *            排序的字段
     * @return {@linkplain Sort}
     */
    public Sort desc(String... name) {
        return new Sort(Direction.DESC, name);
    }

    /**
     * 将传入的{@linkplain Bond}用{@linkplain Operator#AND}关联起来
     * 
     * @param bonds
     *            待关联的{@linkplain Bond}
     * @return and条件的Bond
     */
    public Bond and(Bond... bonds) {
        return Bonds.and(bonds);
    }

    /**
     * 将传入的{@linkplain Bond}用{@linkplain Operator#OR}关联起来
     * 
     * @param bonds
     *            待关联的{@linkplain Bond}
     * @return or条件的Bond
     */
    public Bond or(Bond... bonds) {
        return Bonds.or(bonds);
    }

}

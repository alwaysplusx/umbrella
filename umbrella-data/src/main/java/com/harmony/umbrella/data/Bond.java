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
package com.harmony.umbrella.data;

import java.io.Serializable;

/**
 * 基础查询条件Bond
 * 
 * @author wuxii@foxmail.com
 */
public interface Bond extends Serializable {

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
    Link getLink();

    /**
     * 对当前{@linkplain Bond}取反, 取反后返回新对象. 当前对象不受影响
     * 
     * @return
     */
    Bond not();

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

    /**
     * 将{@linkplain Bond}转为最小单位的sql语句
     * 
     * @return sql
     */
    String toSQL();

    /**
     * 将{@linkplain Bond}转为最小单位的sql语句, 并带有表的别名
     * 
     * @param tableAlias
     *            表的别名
     * @return sql
     */
    String toSQL(String tableAlias);

    /**
     * 将{@linkplain Bond}转为最小单位的JPQL等类型的查询语句。 名称占位符
     * 
     * @param nameAlias
     *            字段别名
     * @return jpql, xql
     */
    String toXQL(String nameAlias);

    /**
     * 将{@linkplain Bond}转为最小单位的JPQL等类型的查询语句。 名称占位符
     * 
     * @param tableAlias
     *            表别名
     * @param nameAlias
     *            字段别名
     * @return jpql, xql
     */
    String toXQL(String tableAlias, String nameAlias);

    /**
     * SQL, JPQL中的连接关系
     * 
     * @author wuxii@foxmail.com
     */
    enum Link {

        EQUAL {

            @Override
            public Link negated() {
                return NOT_EQUAL;
            }

            @Override
            public String desc() {
                return "=";
            }

            @Override
            public String shortName() {
                return "eq";
            }
        },
        NOT_EQUAL {

            @Override
            public Link negated() {
                return EQUAL;
            }

            @Override
            public String desc() {
                return "<>";
            }

            @Override
            public String shortName() {
                return "ne";
            }
        },
        LESS_THAN {

            @Override
            public Link negated() {
                return GREATER_THAN_OR_EQUAL;
            }

            @Override
            public String desc() {
                return "<";
            }

            @Override
            public String shortName() {
                return "lt";
            }
        },
        LESS_THAN_OR_EQUAL {

            @Override
            public Link negated() {
                return GREATER_THAN;
            }

            @Override
            public String desc() {
                return "<=";
            }

            @Override
            public String shortName() {
                return "le";
            }
        },
        GREATER_THAN {

            @Override
            public Link negated() {
                return LESS_THAN_OR_EQUAL;
            }

            @Override
            public String desc() {
                return ">";
            }

            @Override
            public String shortName() {
                return "gt";
            }
        },
        GREATER_THAN_OR_EQUAL {

            @Override
            public Link negated() {
                return LESS_THAN;
            }

            @Override
            public String desc() {
                return ">=";
            }

            @Override
            public String shortName() {
                return "ge";
            }
        },
        IN {

            @Override
            public Link negated() {
                return NOT_IN;
            }

            @Override
            public String desc() {
                return "in";
            }

            @Override
            public String shortName() {
                return "in";
            }
        },
        NOT_IN {

            @Override
            public Link negated() {
                return IN;
            }

            @Override
            public String desc() {
                return "not in";
            }

            @Override
            public String shortName() {
                return "ni";
            }
        },
        NULL {

            @Override
            public Link negated() {
                return NOT_NULL;
            }

            @Override
            public String desc() {
                return "is null";
            }

            @Override
            public String shortName() {
                return "uu";
            }
        },
        NOT_NULL {

            @Override
            public Link negated() {
                return NULL;
            }

            @Override
            public String desc() {
                return "is not null";
            }

            @Override
            public String shortName() {
                return "nu";
            }
        },
        LIKE {

            @Override
            public Link negated() {
                return NOT_LIKE;
            }

            @Override
            public String desc() {
                return "like";
            }

            @Override
            public String shortName() {
                return "ll";
            }
        },
        NOT_LIKE {

            @Override
            public Link negated() {
                return LIKE;
            }

            @Override
            public String desc() {
                return "not like";
            }

            @Override
            public String shortName() {
                return "nl";
            }
        };

        /**
         * 对当前的link取反
         * 
         * @return 当前Link的反义
         */
        public abstract Link negated();

        /**
         * 连接关系的字符
         * 
         * @return 连接字符
         */
        public abstract String desc();

        public abstract String shortName();

    }

}
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

    String toSQL();

    String toSQL(String tableAlias);

    String toXQL(String nameAlias);

    String toXQL(String tableAlias, String nameAlias);

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
        };

        public abstract Link negated();

        public abstract String desc();

    }

}
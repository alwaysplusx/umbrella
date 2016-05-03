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
    public static enum Link {

        EQUAL("=", "eq") {

            @Override
            public Link negated() {
                return NOT_EQUAL;
            }

        },
        NOT_EQUAL("<>", "ne") {

            @Override
            public Link negated() {
                return EQUAL;
            }

        },
        LESS_THAN("<", "lt") {

            @Override
            public Link negated() {
                return GREATER_THAN_OR_EQUAL;
            }

        },
        LESS_THAN_OR_EQUAL("<=", "le") {

            @Override
            public Link negated() {
                return GREATER_THAN;
            }

        },
        GREATER_THAN(">", "gt") {

            @Override
            public Link negated() {
                return LESS_THAN_OR_EQUAL;
            }

        },
        GREATER_THAN_OR_EQUAL(">=", "ge") {

            @Override
            public Link negated() {
                return LESS_THAN;
            }

        },
        IN("in", "in") {

            @Override
            public Link negated() {
                return NOT_IN;
            }

        },
        NOT_IN("not in", "ni") {

            @Override
            public Link negated() {
                return IN;
            }

        },
        NULL("is null", "uu") {

            @Override
            public Link negated() {
                return NOT_NULL;
            }

        },
        NOT_NULL("is not null", "nu") {

            @Override
            public Link negated() {
                return NULL;
            }

        },
        LIKE("like", "lk") {

            @Override
            public Link negated() {
                return NOT_LIKE;
            }

        },
        NOT_LIKE("not like", "nl") {

            @Override
            public Link negated() {
                return LIKE;
            }

        };

        private String desc;
        private String shortName;

        private Link(String desc, String shortName) {
            this.desc = desc;
            this.shortName = shortName;
        }

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
        public String desc() {
            return desc;
        }

        public String shortName() {
            return shortName;
        }

    }

}
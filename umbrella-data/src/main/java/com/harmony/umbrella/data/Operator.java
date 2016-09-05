package com.harmony.umbrella.data;

/**
 * @author wuxii@foxmail.com
 */
public enum Operator {

    EQUAL("=", "EQ") {

        @Override
        public Operator negated() {
            return NOT_EQUAL;
        }

    },
    NOT_EQUAL("<>", "NE") {

        @Override
        public Operator negated() {
            return EQUAL;
        }

    },
    LESS_THAN("<", "LT") {

        @Override
        public Operator negated() {
            return GREATER_THAN_OR_EQUAL;
        }

    },
    LESS_THAN_OR_EQUAL("<=", "LE") {

        @Override
        public Operator negated() {
            return GREATER_THAN;
        }

    },
    GREATER_THAN(">", "GT") {

        @Override
        public Operator negated() {
            return LESS_THAN_OR_EQUAL;
        }

    },
    GREATER_THAN_OR_EQUAL(">=", "GE") {

        @Override
        public Operator negated() {
            return LESS_THAN;
        }

    },
    IN("in", "IN") {

        @Override
        public Operator negated() {
            return NOT_IN;
        }

    },
    NOT_IN("not in", "NOTIN") {

        @Override
        public Operator negated() {
            return IN;
        }

    },
    LIKE("like", "LIKE") {

        @Override
        public Operator negated() {
            return NOT_LIKE;
        }

    },
    NOT_LIKE("not like", "NOTLIKE") {

        @Override
        public Operator negated() {
            return LIKE;
        }

    },
    NULL("is null", "NULL") {

        @Override
        public Operator negated() {
            return NOT_NULL;
        }

    },
    NOT_NULL("is not null", "NOTNULL") {

        @Override
        public Operator negated() {
            return NULL;
        }

    },;

    private String operator;
    private String name;

    private Operator(String operator, String name) {
        this.operator = operator;
        this.name = name;
    }

    /**
     * 对当前的Operator取反
     * 
     * @return 当前Operator的反义
     */
    public abstract Operator negated();

    /**
     * sql对应的操作符
     * 
     * @return 操作符
     */
    public String operator() {
        return operator;
    }

    public String getName() {
        return name;
    }

}

package com.harmony.umbrella.data;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import com.harmony.umbrella.util.StringUtils;

public enum Operator {

    EQUAL("=", "EQ") {

        @Override
        public Operator negated() {
            return NOT_EQUAL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.equal(x, y);
        }

    },
    NOT_EQUAL("<>", "NE") {

        @Override
        public Operator negated() {
            return EQUAL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.notEqual(x, y);
        }

    },
    LESS_THAN("<", "LT") {

        @Override
        public Operator negated() {
            return GREATER_THAN_OR_EQUAL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.lessThan(x, (Comparable) y);
        }

    },
    LESS_THAN_OR_EQUAL("<=", "LE") {

        @Override
        public Operator negated() {
            return GREATER_THAN;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.lessThanOrEqualTo(x, (Comparable) y);
        }

    },
    GREATER_THAN(">", "GT") {

        @Override
        public Operator negated() {
            return LESS_THAN_OR_EQUAL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.greaterThan(x, (Comparable) y);
        }

    },
    GREATER_THAN_OR_EQUAL(">=", "GE") {

        @Override
        public Operator negated() {
            return LESS_THAN;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.greaterThanOrEqualTo(x, (Comparable) y);
        }

    },
    IN("in", "IN") {

        @Override
        public Operator negated() {
            return NOT_IN;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return x.in(inValue(y));
        }

    },
    NOT_IN("not in", "NOTIN") {

        @Override
        public Operator negated() {
            return IN;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.not(x).in(inValue(y));
        }

    },
    LIKE("like", "LIKE") {

        @Override
        public Operator negated() {
            return NOT_LIKE;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.like(x, likeValue(y));
        }

    },
    NOT_LIKE("not like", "NOTLIKE") {

        @Override
        public Operator negated() {
            return LIKE;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.notLike(x, likeValue(y));
        }

    },
    NULL("is null", "IS_NULL") {

        @Override
        public Operator negated() {
            return NOT_NULL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.isNull(x);
        }

    },
    NOT_NULL("is not null", "IS_NOTNULL") {

        @Override
        public Operator negated() {
            return NULL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.isNotNull(x);
        }

    },
    TRUE("is true", "IS_TRUE") {

        @Override
        public Operator negated() {
            return FALSE;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.isTrue(x);
        }

    },
    FALSE("is false", "IS_FALSE") {

        @Override
        public Operator negated() {
            return TRUE;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.isFalse(x);
        }

    },
    SIZE_OF("size", "SIZE") {

        @Override
        public Operator negated() {
            return NOT_SIZE_OF;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.equal(cb.size(x), y);
        }

    },
    NOT_SIZE_OF("not size", "NOTSIZE") {

        @Override
        public Operator negated() {
            return SIZE_OF;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.notEqual(cb.size(x), y);
        }

    };

    private static Collection<?> inValue(Object y) {
        if (y instanceof String) {
            return Arrays.asList(StringUtils.tokenizeToStringArray((String) y, ","));
        } else if (y instanceof Collection) {
            return (Collection) y;
        } else if (y instanceof Object[]) {
            return Arrays.asList((Object[]) y);
        }
        throw new IllegalArgumentException("in value is not object array/string/collection");
    }

    private static String likeValue(Object y) {
        if (y instanceof String) {
            if (((String) y).indexOf("%") == -1) {
                y = "%" + y + "%";
            }
            return (String) y;
        }
        throw new IllegalArgumentException("like value is not string");
    }

    private String symbol;
    private String qualifiedName;

    private Operator(String symbol, String qualifiedName) {
        this.symbol = symbol;
        this.qualifiedName = qualifiedName;
    }

    /**
     * 对当前的Operator取反
     * 
     * @return 当前Operator的反义
     */
    public abstract Operator negated();

    public abstract Predicate explain(Expression x, CriteriaBuilder cb, Object y);

    /**
     * sql对应的操作符
     * 
     * @return 操作符
     */
    public String symbol() {
        return symbol;
    }

    /**
     * 限定名
     * 
     * @return 限定名
     */
    public String qualifiedName() {
        return qualifiedName;
    }

    public static Operator forName(String qualifiedName) {
        if (qualifiedName == null) {
            return null;
        }
        Operator[] values = Operator.values();
        for (Operator operator : values) {
            if (qualifiedName.equals(operator.qualifiedName)) {
                return operator;
            }
        }
        return null;
    }

}
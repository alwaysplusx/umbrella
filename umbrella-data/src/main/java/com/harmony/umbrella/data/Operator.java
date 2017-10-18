package com.harmony.umbrella.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.util.Assert;

import com.harmony.umbrella.util.StringUtils;

public enum Operator implements ExpressionExplainer {

    EQUAL("=", "EQ") {

        @Override
        public Operator negated() {
            return NOT_EQUAL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return y instanceof Expression ? cb.equal(x, (Expression) y) : cb.equal(x, y);
        }

    },
    NOT_EQUAL("<>", "NE") {

        @Override
        public Operator negated() {
            return EQUAL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return y instanceof Expression ? cb.notEqual(x, (Expression) y) : cb.notEqual(x, y);
        }

    },
    LESS_THAN("<", "LT") {

        @Override
        public Operator negated() {
            return GREATER_THAN_OR_EQUAL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return y instanceof Expression ? cb.lessThan(x, (Expression) y) : cb.lessThan(x, (Comparable) y);
        }

    },
    LESS_THAN_OR_EQUAL("<=", "LE") {

        @Override
        public Operator negated() {
            return GREATER_THAN;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return y instanceof Expression ? cb.lessThanOrEqualTo(x, (Expression) y) : cb.lessThanOrEqualTo(x, (Comparable) y);
        }

    },
    GREATER_THAN(">", "GT") {

        @Override
        public Operator negated() {
            return LESS_THAN_OR_EQUAL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return y instanceof Expression ? cb.greaterThan(x, (Expression) y) : cb.greaterThan(x, (Comparable) y);
        }

    },
    GREATER_THAN_OR_EQUAL(">=", "GE") {

        @Override
        public Operator negated() {
            return LESS_THAN;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return y instanceof Expression ? cb.greaterThanOrEqualTo(x, (Expression) y) : cb.greaterThanOrEqualTo(x, (Comparable) y);
        }

    },
    IN("in", "IN") {

        @Override
        public Operator negated() {
            return NOT_IN;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            if (y instanceof Expression) {
                return x.in((Expression) y);
            }
            List<Collection> v = cuttingBySize(inValue(y), 999);
            Iterator<Collection> it = v.iterator();
            Predicate predicate = x.in(it.next());
            for (; it.hasNext();) {
                predicate = cb.or(predicate, x.in(it.next()));
            }
            return predicate;
        }

    },
    NOT_IN("not in", "NOTIN") {

        @Override
        public Operator negated() {
            return IN;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            if (y instanceof Expression) {
                return x.in((Expression) y);
            }
            List<Collection> v = cuttingBySize(inValue(y), 999);
            Iterator<Collection> it = v.iterator();
            Predicate predicate = x.in(it.next()).not();
            for (; it.hasNext();) {
                predicate = cb.and(predicate, x.in(it.next()).not());
            }
            return predicate;
        }

    },
    LIKE("like", "LIKE") {

        @Override
        public Operator negated() {
            return NOT_LIKE;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            if (y instanceof Expression) {
                return cb.like(x, (Expression) y);
            }
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
            if (y instanceof Expression) {
                return cb.notLike(x, (Expression) y);
            }
            return cb.notLike(x, likeValue(y));
        }

    },
    NULL("is null", "NULL") {

        @Override
        public Operator negated() {
            return NOT_NULL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.isNull(x);
        }

    },
    NOT_NULL("is not null", "NOTNULL") {

        @Override
        public Operator negated() {
            return NULL;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.isNotNull(x);
        }

    },
    TRUE("is true", "TRUE") {

        @Override
        public Operator negated() {
            return FALSE;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return cb.isTrue(x);
        }

    },
    FALSE("is false", "FALSE") {

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
            return y instanceof Expression ? cb.equal(cb.size(x), (Expression) y) : cb.equal(cb.size(x), y);
        }

    },
    NOT_SIZE_OF("not size", "NOTSIZE") {

        @Override
        public Operator negated() {
            return SIZE_OF;
        }

        @Override
        public Predicate explain(Expression x, CriteriaBuilder cb, Object y) {
            return y instanceof Expression ? cb.notEqual(cb.size(x), (Expression) y) : cb.notEqual(cb.size(x), y);
        }

    };

    static List<Collection> cuttingBySize(Collection v, int size) {
        final int length = v.size();
        if (length <= size) {
            return Arrays.asList(v);
        }
        Object[] array = v.toArray();
        List<Collection> result = new ArrayList<Collection>((length / size) + 1);
        for (int start = 0, end = size; start < length;) {
            final int copyLength = end - start;
            Object[] tmp = new Object[copyLength];
            System.arraycopy(array, start, tmp, 0, copyLength);
            result.add(Arrays.asList(tmp));
            if (end == length) {
                break;
            }
            start = end;
            end = (start + size) > length ? length : (start + size);
        }
        return result;
    }

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

    @Override
    public String toString() {
        return symbol;
    }

    public static Operator forName(String qualifiedName) {
        Assert.notNull(qualifiedName, "name not allow null");
        Operator[] values = Operator.values();
        for (Operator operator : values) {
            if (qualifiedName.equals(operator.qualifiedName)) {
                return operator;
            }
        }
        throw new IllegalArgumentException(qualifiedName + " not found!");
    }

}
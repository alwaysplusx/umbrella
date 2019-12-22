package com.harmony.umbrella.data;

import com.harmony.umbrella.data.model.ExpressionModel;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.*;

public enum Operator implements ExpressionOperator {

    EQUAL("=", "EQ") {
        @Override
        public Operator negated() {
            return NOT_EQUAL;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            return y instanceof Expression
                    ? cb.equal(xExp, (Expression) y)
                    : cb.equal(xExp, y);
        }

    },
    NOT_EQUAL("<>", "NE") {
        @Override
        public Operator negated() {
            return EQUAL;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            return y instanceof Expression
                    ? cb.notEqual(xExp, (Expression) y)
                    : cb.notEqual(xExp, y);
        }

    },
    LESS_THAN("<", "LT") {
        @Override
        public Operator negated() {
            return GREATER_THAN_OR_EQUAL;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            return y instanceof Expression
                    ? cb.lessThan(xExp, (Expression) y)
                    : cb.lessThan(xExp, (Comparable) y);
        }

    },
    LESS_THAN_OR_EQUAL("<=", "LE") {
        @Override
        public Operator negated() {
            return GREATER_THAN;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            return y instanceof Expression
                    ? cb.lessThanOrEqualTo(xExp, (Expression) y)
                    : cb.lessThanOrEqualTo(xExp, (Comparable) y);
        }

    },
    GREATER_THAN(">", "GT") {
        @Override
        public Operator negated() {
            return LESS_THAN_OR_EQUAL;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            return y instanceof Expression
                    ? cb.greaterThan(xExp, (Expression) y)
                    : cb.greaterThan(xExp, (Comparable) y);
        }

    },
    GREATER_THAN_OR_EQUAL(">=", "GE") {
        @Override
        public Operator negated() {
            return LESS_THAN;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            return y instanceof Expression
                    ? cb.greaterThanOrEqualTo(xExp, (Expression) y)
                    : cb.greaterThanOrEqualTo(xExp, (Comparable) y);
        }

    },
    IN("in", "IN") {
        @Override
        public Operator negated() {
            return NOT_IN;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            if (y instanceof Expression) {
                return xExp.in((Expression) y);
            }
            List<Collection> v = cuttingBySize(inValue(y), 999);
            Iterator<Collection> it = v.iterator();
            Predicate predicate = xExp.in(it.next());
            for (; it.hasNext(); ) {
                predicate = cb.or(predicate, xExp.in(it.next()));
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
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            if (y instanceof Expression) {
                return xExp.in((Expression) y);
            }
            List<Collection> v = cuttingBySize(inValue(y), 999);
            Iterator<Collection> it = v.iterator();
            Predicate predicate = xExp.in(it.next()).not();
            for (; it.hasNext(); ) {
                predicate = cb.and(predicate, xExp.in(it.next()).not());
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
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            return y instanceof Expression
                    ? cb.like(xExp, (Expression) y)
                    : cb.like(xExp, String.valueOf(y));
        }

    },
    NOT_LIKE("not like", "NOTLIKE") {
        @Override
        public Operator negated() {
            return LIKE;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = x.getExpression();
            return y instanceof Expression
                    ? cb.notLike(xExp, (Expression) y)
                    : cb.notLike(xExp, String.valueOf(y));
        }

    },
    NULL("is null", "NULL") {
        @Override
        public Operator negated() {
            return NOT_NULL;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            return cb.isNull(x.getExpression());
        }

    },
    NOT_NULL("is not null", "NOTNULL") {
        @Override
        public Operator negated() {
            return NULL;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            return cb.isNotNull(x.getExpression());
        }

    },
    TRUE("is true", "TRUE") {
        @Override
        public Operator negated() {
            return FALSE;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            return cb.isTrue(x.getExpression());
        }

    },
    FALSE("is false", "FALSE") {
        @Override
        public Operator negated() {
            return TRUE;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            return cb.isFalse(x.getExpression());
        }

    },
    SIZE_OF("size", "SIZE") {
        @Override
        public Operator negated() {
            return NOT_SIZE_OF;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = cb.size(x.getExpression());
            return y instanceof Expression
                    ? cb.equal(xExp, (Expression) y)
                    : cb.equal(xExp, y);
        }

    },
    NOT_SIZE_OF("not size", "NOTSIZE") {
        @Override
        public Operator negated() {
            return SIZE_OF;
        }

        @Override
        public Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb) {
            Expression xExp = cb.size(x.getExpression());
            return y instanceof Expression
                    ? cb.notEqual(xExp, (Expression) y)
                    : cb.notEqual(xExp, y);
        }

    };

    static List<Collection> cuttingBySize(Collection v, int size) {
        final int length = v.size();
        if (length <= size) {
            return Arrays.asList(v);
        }
        Object[] array = v.toArray();
        List<Collection> result = new ArrayList<>((length / size) + 1);
        for (int start = 0, end = size; start < length; ) {
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

    private String symbol;
    private String qualifiedName;

    Operator(String symbol, String qualifiedName) {
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
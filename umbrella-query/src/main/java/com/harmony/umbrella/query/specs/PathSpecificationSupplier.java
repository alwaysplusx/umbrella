package com.harmony.umbrella.query.specs;

import com.harmony.umbrella.query.CriteriaDefinition.Comparator;
import com.harmony.umbrella.query.Path;
import com.harmony.umbrella.query.SpecificationSupplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.*;

public class PathSpecificationSupplier<T> implements SpecificationSupplier<T> {

    private Path path;
    private Object value;
    private ComparatorResolver comparatorResolver;

    public PathSpecificationSupplier(Path<T> path, Object value, Comparator comparator) {
        this.path = path;
        this.value = value;
        this.comparatorResolver = ComparatorResolver.of(comparator);
    }

    @NotNull
    @Override
    public Specification<T> get() {
        return (Specification<T>) (root, query, cb) -> comparatorResolver.resolve(root.get(path.getColumn()), value, cb);
    }

    private static Collection<?> toInValues(Object y) {
        if (y instanceof String) {
            return Arrays.asList(StringUtils.tokenizeToStringArray((String) y, ","));
        } else if (y instanceof Collection) {
            return (Collection) y;
        } else if (y instanceof Object[]) {
            return Arrays.asList((Object[]) y);
        }
        throw new IllegalArgumentException("in value is not object array/string/collection");
    }

    static List<Collection> cuttingBySize(Collection v, int size) {
        final int length = v.size();
        if (length <= size) {
            return Collections.singletonList(v);
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
            end = Math.min((start + size), length);
        }
        return result;
    }

    enum ComparatorResolver {

        EQ(Comparator.EQ) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return right instanceof Expression
                        ? cb.equal(left, (Expression<?>) right)
                        : cb.equal(left, right);
            }
        },
        NEQ(Comparator.NEQ) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return right instanceof Expression
                        ? cb.notEqual(left, (Expression<?>) right)
                        : cb.notEqual(left, right);
            }
        },
        LT(Comparator.LT) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return right instanceof Expression
                        ? cb.lessThan(left, (Expression) right)
                        : cb.lessThan(left, (Comparable) right);
            }
        },
        LTE(Comparator.LTE) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return right instanceof Expression
                        ? cb.lessThanOrEqualTo(left, (Expression) right)
                        : cb.lessThanOrEqualTo(left, (Comparable) right);
            }
        },
        GT(Comparator.GT) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return right instanceof Expression
                        ? cb.greaterThan(left, (Expression) right)
                        : cb.greaterThan(left, (Comparable) right);
            }
        },
        GTE(Comparator.GTE) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return right instanceof Expression
                        ? cb.greaterThanOrEqualTo(left, (Expression) right)
                        : cb.greaterThanOrEqualTo(left, (Comparable) right);
            }
        },
        IS_NULL(Comparator.IS_NULL) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return cb.isNull(left);
            }
        },
        IS_NOT_NULL(Comparator.IS_NOT_NULL) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return cb.isNotNull(left);
            }
        },
        LIKE(Comparator.LIKE) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return right instanceof Expression
                        ? cb.like(left, (Expression) right)
                        : cb.like(left, right.toString());
            }
        },
        NOT_LIKE(Comparator.NOT_LIKE) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return right instanceof Expression
                        ? cb.notLike(left, (Expression) right)
                        : cb.notLike(left, right.toString());
            }
        },
        NOT_IN(Comparator.NOT_IN) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                Collection<?> values = toInValues(right);
                if (values.isEmpty()) {
                    return cb.conjunction();
                }
                List<Collection> v = cuttingBySize(values, 999);
                Iterator<Collection> it = v.iterator();
                Predicate predicate = left.in(it.next()).not();
                for (; it.hasNext(); ) {
                    predicate = cb.and(predicate, left.in(it.next()).not());
                }
                return predicate;
            }
        },
        IN(Comparator.IN) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                Collection<?> values = toInValues(right);
                if (values.isEmpty()) {
                    return cb.conjunction();
                }
                List<Collection> v = cuttingBySize(values, 999);
                Iterator<Collection> it = v.iterator();
                Predicate predicate = left.in(it.next());
                for (; it.hasNext(); ) {
                    predicate = cb.or(predicate, left.in(it.next()));
                }
                return predicate;
            }
        },
        IS_TRUE(Comparator.IS_TRUE) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return cb.isTrue(left);
            }
        },
        IS_FALSE(Comparator.IS_FALSE) {
            @Override
            public Predicate resolve(Expression left, Object right, CriteriaBuilder cb) {
                return cb.isFalse(left);
            }
        };

        public final Comparator comparator;

        ComparatorResolver(Comparator comparator) {
            this.comparator = comparator;
        }

        public abstract Predicate resolve(Expression left, Object right, CriteriaBuilder cb);

        public static ComparatorResolver of(Comparator comparator) {
            for (ComparatorResolver value : values()) {
                if (value.comparator == comparator) {
                    return value;
                }
            }
            throw new IllegalArgumentException("unknown comparator type " + comparator);
        }

    }

}

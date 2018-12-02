package com.harmony.umbrella.data;

import javax.persistence.EntityManager;

/**
 * @author wuxii@foxmail.com
 */
public class JpaQueryBuilder<M> extends QueryBuilder<JpaQueryBuilder<M>, M> {

    private static final long serialVersionUID = 1L;

    public static <T> JpaQueryBuilder<T> newBuilder() {
        return new JpaQueryBuilder<>();
    }

    public static <T> JpaQueryBuilder<T> newBuilder(Class<T> domainClass) {
        return new JpaQueryBuilder<>(domainClass);
    }

    public JpaQueryBuilder() {
    }

    public JpaQueryBuilder(Class<M> domainClass) {
        super(domainClass);
    }

    public JpaQueryBuilder(Class<M> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

    public JpaQueryBuilder(EntityManager entityManager) {
        super(entityManager);
    }

    public DisposableColumn begin(String name) {
        return this.begin().and(name);
    }

    public DisposableColumn and(String name) {
        return new DisposableColumn(name, CompositionType.AND);
    }

    public DisposableColumn or(String name) {
        return new DisposableColumn(name, CompositionType.OR);
    }

    /**
     * 用于创建列条件, 当条件创建后则无法修改或者再使用当前的column来创建新条件
     *
     * @author wuxii@foxmail.com
     */
    public final class DisposableColumn {

        private boolean done;

        private String name;
        private CompositionType compositionType;
        private JpaQueryBuilder<M> builder;

        private DisposableColumn(String name, CompositionType compositionType) {
            this.name = name;
            this.compositionType = compositionType;
            this.builder = JpaQueryBuilder.this;
        }

        public JpaQueryBuilder<M> equal(Object val) {
            return prepare().equal(name, val);
        }

        public JpaQueryBuilder<M> notEqual(Object val) {
            return prepare().notEqual(name, val);
        }

        public JpaQueryBuilder<M> like(Object val) {
            return prepare().like(name, val);
        }

        public JpaQueryBuilder<M> notLike(Object val) {
            return prepare().notLike(name, val);
        }

        public JpaQueryBuilder<M> in(Object val) {
            return prepare().in(name, val);
        }

        public JpaQueryBuilder<M> notIn(Object val) {
            return prepare().notIn(name, val);
        }

        public JpaQueryBuilder<M> between(Comparable left, Comparable right) {
            return prepare().between(name, left, right);
        }

        public JpaQueryBuilder<M> notBetween(Comparable left, Comparable right) {
            return prepare().notBetween(name, left, right);
        }

        public JpaQueryBuilder<M> greatThen(Comparable val) {
            return prepare().greatThen(name, val);
        }

        public JpaQueryBuilder<M> greatEqual(Comparable val) {
            return prepare().greatEqual(name, val);
        }

        public JpaQueryBuilder<M> lessThen(Comparable val) {
            return prepare().lessThen(name, val);
        }

        public JpaQueryBuilder<M> lessEqual(Comparable val) {
            return prepare().lessEqual(name, val);
        }

        public JpaQueryBuilder<M> isNull() {
            return prepare().isNull(name);
        }

        public JpaQueryBuilder<M> isNotNull() {
            return prepare().isNotNull(name);
        }

        public JpaQueryBuilder<M> sizeOf(long size) {
            return prepare().sizeOf(name, size);
        }

        public JpaQueryBuilder<M> notSizeOf(long size) {
            return prepare().notSizeOf(name, size);
        }

        private JpaQueryBuilder<M> prepare() {
            if (done) {
                throw new IllegalStateException("one time column just allow build only one specification");
            }
            done = true;
            return compositionType != CompositionType.OR
                    ? builder.and()
                    : builder.or();
        }

    }

}

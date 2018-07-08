package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.CompositionType;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class JpaQueryBuilder<M> extends QueryBuilder<JpaQueryBuilder<M>, M> {

    private static final long serialVersionUID = 1L;

    private static final Log log = Logs.getLog();

    public static <T> JpaQueryBuilder<T> newBuilder() {
        return new JpaQueryBuilder<>();
    }

    public static <T> JpaQueryBuilder<T> newBuilder(Class<T> domainClass) {
        return new JpaQueryBuilder<>(domainClass);
    }

    public JpaQueryBuilder() {
    }

    public JpaQueryBuilder(Class<M> entityClass) {
        super(entityClass);
    }

    public JpaQueryBuilder(Class<M> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    public JpaQueryBuilder(EntityManager entityManager) {
        super(entityManager);
    }

    // subquery

    public <R> SubqueryBuilder<R> subquery(Class<R> clazz) {
        return this.new SubqueryBuilder<R>(clazz, this);
    }

    public OneTimeColumn column(String name) {
        return new OneTimeColumn(name, CompositionType.AND);
    }

    public OneTimeColumn start(String name) {
        start(assembleType == null ? CompositionType.AND : assembleType);
        return new OneTimeColumn(name, CompositionType.AND);
    }

    public OneTimeColumn and(String name) {
        return new OneTimeColumn(name, CompositionType.AND);
    }

    public OneTimeColumn or(String name) {
        return new OneTimeColumn(name, CompositionType.OR);
    }

    /**
     * 用于创建列条件, 当条件创建后则无法修改或者再使用当前的column来创建新条件
     *
     * @author wuxii@foxmail.com
     */
    public final class OneTimeColumn {

        private JpaQueryBuilder<M> parent;

        private boolean added;

        private String name;
        private CompositionType composition;

        private OneTimeColumn(String name, CompositionType composition) {
            this.name = name;
            this.composition = composition;
            this.parent = JpaQueryBuilder.this;
        }

        public JpaQueryBuilder<M> equal(Object val) {
            return addCondition(val, Operator.EQUAL);
        }

        public JpaQueryBuilder<M> notEqual(Object val) {
            return addCondition(val, Operator.NOT_EQUAL);
        }

        public JpaQueryBuilder<M> like(Object val) {
            return addCondition(val, Operator.LIKE);
        }

        public JpaQueryBuilder<M> notLike(Object val) {
            return addCondition(val, Operator.NOT_LIKE);
        }

        public JpaQueryBuilder<M> in(Object val) {
            return addCondition(val, Operator.IN);
        }

        public JpaQueryBuilder<M> notIn(Object val) {
            return addCondition(val, Operator.NOT_IN);
        }

        // between and notBewteen 需要添加两个条件, 添加一条后将临时变量置为false
        public JpaQueryBuilder<M> between(Object left, Object right) {
            greatEqual(left);
            this.added = false;
            lessEqual(right);
            return parent;
        }

        public JpaQueryBuilder<M> notBetween(Object left, Object right) {
            lessThen(left);
            this.added = false;
            greatThen(right);
            return parent;
        }

        public JpaQueryBuilder<M> greatThen(Object val) {
            return addCondition(val, Operator.GREATER_THAN);
        }

        public JpaQueryBuilder<M> greatEqual(Object val) {
            return addCondition(val, Operator.GREATER_THAN_OR_EQUAL);
        }

        public JpaQueryBuilder<M> lessThen(Object val) {
            return addCondition(val, Operator.LESS_THAN);
        }

        public JpaQueryBuilder<M> lessEqual(Object val) {
            return addCondition(val, Operator.LESS_THAN_OR_EQUAL);
        }

        public JpaQueryBuilder<M> isNull() {
            return addCondition(null, Operator.NULL);
        }

        public JpaQueryBuilder<M> isNotNull() {
            return addCondition(null, Operator.NOT_NULL);
        }

        public JpaQueryBuilder<M> sizeOf(int size) {
            return addCondition(size, Operator.SIZE_OF);
        }

        public JpaQueryBuilder<M> notSizeOf(int size) {
            return addCondition(size, Operator.NOT_SIZE_OF);
        }

        private JpaQueryBuilder<M> addCondition(Object val, Operator operator) {
            if (!this.added) {
                parent.assembleType = composition;
                if (val instanceof JpaQueryBuilder.OneTimeColumn) {
                    parent.addExpressionCondition(name, ((OneTimeColumn) val).name, operator);
                } else {
                    parent.addCondition(name, val, operator);
                }
                this.added = true;
            } else {
                throw new IllegalStateException("one time column just allow build only one condition");
            }
            return parent;
        }

    }

    public final class SubqueryBuilder<R> extends QueryBuilder<SubqueryBuilder<R>, R> {

        private static final long serialVersionUID = -7997630445529853487L;

        protected final QueryBuilder parentQueryBuilder;
        protected Selections subSelections;

        protected SubqueryBuilder(Class<R> entityClass, QueryBuilder parent) {
            super(parent.entityManager);
            this.parentQueryBuilder = parent;
            this.domainClass = entityClass;
            this.assembleType = parentQueryBuilder.assembleType;
            this.autoEnclose = parentQueryBuilder.autoEnclose;
            this.strictMode = parentQueryBuilder.strictMode;
        }

        public SubqueryBuilder<R> select(String column) {
            this.subSelections = Selections.of(column);
            return this;
        }

        public SubqueryBuilder<R> selectFunction(String function, String column) {
            this.subSelections = Selections.function(function, column);
            return this;
        }

        public JpaQueryBuilder<M> apply(final String function, final String parentColumn, Operator operator) {
            return apply(Selections.function(function, parentColumn), operator);
        }

        public JpaQueryBuilder<M> apply(final String parentColumn, final Operator operator) {
            return apply(Selections.of(parentColumn), operator);
        }

        private JpaQueryBuilder<M> apply(final Selections parentSelections, final Operator operator) {
            this.finishQuery();
            final Specification subCondition = this.condition;
            final Selections subSelections = this.subSelections;
            if (this.grouping != null && !this.grouping.isEmpty() //
                    || this.sort != null //
                    || this.fetchAttributes != null //
                    || this.joinAttributes != null) {
                log.warn("Subquery not support grouping/orderBy/fetch/join");
            }
            return (JpaQueryBuilder<M>) parentQueryBuilder.addSpecification(new Specification<M>() {
                /**
                 *
                 */
                private static final long serialVersionUID = -7179840312915716364L;

                @Override
                public Predicate toPredicate(Root<M> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Subquery<R> subquery = query.subquery(domainClass);
                    Root<R> subRoot = subquery.from(domainClass);
                    subquery.where(subCondition.toPredicate(subRoot, null, cb));
                    List<Expression> subs = subSelections.select(subRoot, query, cb);
                    List<Expression> parents = parentSelections.select(root, query, cb);
                    return operator.explain(parents.get(0), cb, subs.get(0));
                }
            });
        }

    }
}

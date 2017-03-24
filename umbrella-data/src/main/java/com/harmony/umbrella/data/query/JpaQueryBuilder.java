package com.harmony.umbrella.data.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.QueryResult.Selections;
import com.harmony.umbrella.data.util.QueryUtils;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class JpaQueryBuilder<M> extends QueryBuilder<JpaQueryBuilder<M>, M> {

    private static final long serialVersionUID = 1L;

    private static final Log log = Logs.getLog();

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

    public final class SubqueryBuilder<R> extends QueryBuilder<SubqueryBuilder<R>, R> {

        private static final long serialVersionUID = -7997630445529853487L;

        protected final QueryBuilder parentQueryBuilder;
        protected Selections subSelections;

        protected SubqueryBuilder(Class<R> entityClass, QueryBuilder parent) {
            super(parent.entityManager);
            this.parentQueryBuilder = parent;
            this.entityClass = entityClass;
            this.assembleType = parentQueryBuilder.assembleType;
            this.autoEnclose = parentQueryBuilder.autoEnclose;
            this.strictMode = parentQueryBuilder.strictMode;
        }

        public SubqueryBuilder<R> select(String column) {
            this.subSelections = QueryUtils.select(column);
            return this;
        }

        public SubqueryBuilder<R> selectFunction(String function, String column) {
            this.subSelections = QueryUtils.select(function, column);
            return this;
        }

        public JpaQueryBuilder<M> apply(final String function, final String parentColumn, Operator operator) {
            return apply(QueryUtils.select(function, parentColumn), operator);
        }

        public JpaQueryBuilder<M> apply(final String parentColumn, final Operator operator) {
            return apply(QueryUtils.select(parentColumn), operator);
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
            return (JpaQueryBuilder<M>) parentQueryBuilder.addSpecication(new Specification<M>() {
                @Override
                public Predicate toPredicate(Root<M> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Subquery<R> subquery = query.subquery(entityClass);
                    Root<R> subRoot = subquery.from(entityClass);
                    subquery.where(subCondition.toPredicate(subRoot, null, cb));
                    List<Expression> subs = subSelections.selection(subRoot, cb);
                    List<Expression> parents = parentSelections.selection(root, cb);
                    return operator.explain(parents.get(0), cb, subs.get(0));
                }
            });
        }

    }
}

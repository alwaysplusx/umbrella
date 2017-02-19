package com.harmony.umbrella.data.query;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.util.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JpaQueryBuilder<M> extends QueryBuilder<JpaQueryBuilder<M>, M> {

    private static final long serialVersionUID = 1L;

    public JpaQueryBuilder() {
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

        protected String column;

        private String function;

        protected SubqueryBuilder(Class<R> entityClass, QueryBuilder parent) {
            super(parent.entityManager);
            this.parentQueryBuilder = parent;
            this.entityClass = entityClass;
            this.assembleType = parentQueryBuilder.assembleType;
            this.autoEnclose = parentQueryBuilder.autoEnclose;
            this.strictMode = parentQueryBuilder.strictMode;
        }

        public SubqueryBuilder<R> select(String column) {
            this.column = column;
            return this;
        }

        public SubqueryBuilder<R> selectFunction(String function, String column) {
            this.function = function;
            this.column = column;
            return this;
        }

        public JpaQueryBuilder<M> apply(final String column, final Operator operator) {
            this.finishQuery();
            final Specification<R> subCondition = this.specification;
            return (JpaQueryBuilder<M>) parentQueryBuilder.addSpecication(new Specification<M>() {
                @Override
                public Predicate toPredicate(Root<M> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    final String subColumn = SubqueryBuilder.this.column;
                    Subquery<R> subquery = query.subquery(entityClass);
                    Root<R> subRoot = subquery.from(entityClass);
                    subquery.where(subCondition.toPredicate(subRoot, null, cb));
                    Expression subExpression = QueryUtils.toExpressionRecursively(subRoot, subColumn);
                    if (function != null) {
                        subExpression = cb.function(function, null, subExpression);
                    }
                    subquery.select(subExpression);
                    Expression parentExpression = QueryUtils.toExpressionRecursively(root, column);
                    return operator.explain(parentExpression, cb, subquery);
                }
            });
        }
    }
}

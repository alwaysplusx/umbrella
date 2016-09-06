package com.harmony.umbrella.data.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.Logical;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Sort.Direction;
import com.harmony.umbrella.data.domain.Sort.Order;
import com.harmony.umbrella.data.domain.Specifications;
import com.harmony.umbrella.data.util.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBuilder<T extends QueryBuilder<T>> {

    protected transient EntityManager entityManager;
    protected transient CriteriaBuilder builder;
    protected transient Stack<Bind> queryStack = new Stack<Bind>();

    protected boolean autoStart = true;
    protected boolean autoFinish = true;

    protected boolean allowEmptyCondition;

    protected Class entityClass;
    private Specification specification;

    protected List<Order> orders = new ArrayList<Order>();

    // query property

    public T withEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return (T) this;
    }

    public T withEntityClass(Class<?> entityClass) {
        return form(entityClass);
    }

    public T form(Class<?> entityClass) {
        this.entityClass = entityClass;
        return (T) this;
    }

    // result

    public <E> E getSingleResult() {
        prepareQuery();
        return null;
    }

    public <E> E getFirstResult() {
        return null;
    }

    public <E> List<E> getResultList() {
        return null;
    }

    public <E> Page<E> getResultPage() {
        return null;
    }

    // sort

    public T asc(String... name) {
        return order(Direction.ASC, name);
    }

    public T desc(String... name) {
        return order(Direction.DESC, name);
    }

    public T order(Direction dir, String... name) {
        for (String p : name) {
            this.orders.add(new Order(dir, p));
        }
        return (T) this;
    }

    // paging

    // query condition method

    public T equal(String name, Object value) {
        return and(name, value, Operator.EQUAL);
    }

    public T notEqual(String name, Object value) {
        return and(name, value, Operator.NOT_EQUAL);
    }

    public T like(String name, Object value) {
        return and(name, value, Operator.LIKE);
    }

    public T notLike(String name, Object value) {
        return and(name, value, Operator.NOT_LIKE);
    }

    public T in(String name, Object value) {
        return and(name, value, Operator.IN);
    }

    public T in(String name, Collection<?> value) {
        return and(name, value, Operator.IN);
    }

    public T in(String name, Object... value) {
        return and(name, value, Operator.IN);
    }

    public T notIn(String name, Object value) {
        return and(name, value, Operator.NOT_IN);
    }

    public T notIn(String name, Collection<?> value) {
        return and(name, value, Operator.NOT_IN);
    }

    public T notIn(String name, Object... value) {
        return and(name, value, Operator.NOT_IN);
    }

    public T between(String name, Object left, Object right) {
        return start()//
                .and(name, left, Operator.GREATER_THAN_OR_EQUAL)//
                .and(name, right, Operator.LESS_THAN_OR_EQUAL)//
                .end();
    }

    public T notBetween(String name, Object left, Object right) {
        return start()//
                .and(name, left, Operator.LESS_THAN)//
                .and(name, right, Operator.GREATER_THAN)//
                .end();
    }

    public T greatThen(String name, Object value) {
        return and(name, value, Operator.GREATER_THAN);
    }

    public T greatEqual(String name, Object value) {
        return and(name, value, Operator.GREATER_THAN_OR_EQUAL);
    }

    public T lessThen(String name, Object value) {
        return and(name, value, Operator.LESS_THAN);
    }

    public T lessEqual(String name, Object value) {
        return and(name, value, Operator.LESS_THAN_OR_EQUAL);
    }

    public T isNull(String name) {
        return and(name, null, Operator.NULL);
    }

    public T isNotNull(String name) {
        return and(name, null, Operator.NOT_NULL);
    }

    // or condition

    public T orEqual(String name, Object value) {
        return or(name, value, Operator.EQUAL);
    }

    public T orNotEqual(String name, Object value) {
        return or(name, value, Operator.NOT_EQUAL);
    }

    public T orLike(String name, Object value) {
        return or(name, value, Operator.LIKE);
    }

    public T orNotLike(String name, Object value) {
        return or(name, value, Operator.NOT_LIKE);
    }

    public T orIn(String name, Object value) {
        return or(name, value, Operator.IN);
    }

    public T orIn(String name, Collection<?> value) {
        return or(name, value, Operator.IN);
    }

    public T orIn(String name, Object... value) {
        return or(name, value, Operator.IN);
    }

    public T orNotIn(String name, Object value) {
        return or(name, value, Operator.NOT_IN);
    }

    public T orNotIn(String name, Collection<?> value) {
        return or(name, value, Operator.NOT_IN);
    }

    public T orNotIn(String name, Object... value) {
        return or(name, value, Operator.NOT_IN);
    }

    public T orBetween(String name, Object left, Object right) {
        return start(Logical.OR)//
                .and(name, left, Operator.GREATER_THAN_OR_EQUAL)//
                .and(name, right, Operator.LESS_THAN_OR_EQUAL)//
                .end();
    }

    public T orNotBetween(String name, Object left, Object right) {
        return start(Logical.OR)//
                .or(name, left, Operator.LESS_THAN)//
                .or(name, right, Operator.GREATER_THAN)//
                .end();
    }

    public T orGreatThen(String name, Object value) {
        return or(name, value, Operator.GREATER_THAN);
    }

    public T orGreatEqual(String name, Object value) {
        return or(name, value, Operator.GREATER_THAN_OR_EQUAL);
    }

    public T orLessThen(String name, Object value) {
        return or(name, value, Operator.LESS_THAN);
    }

    public T orLessEqual(String name, Object value) {
        return or(name, value, Operator.LESS_THAN_OR_EQUAL);
    }

    public T orIsNull(String name) {
        return or(name, null, Operator.NULL);
    }

    public T orIsNotNull(String name) {
        return or(name, null, Operator.NOT_NULL);
    }

    // add condition method

    public T and(String name, Object value, Operator operator) {
        return addCondition(name, value, operator, Logical.AND);
    }

    public T or(String name, Object value, Operator operator) {
        return addCondition(name, value, operator, Logical.OR);
    }

    public T and(Specification spec) {
        return addSpecification(new SpecificationWrapper(spec, Logical.AND));
    }

    public T or(Specification spec) {
        return addSpecification(new SpecificationWrapper(spec, Logical.OR));
    }

    protected T addCondition(String name, Object value, Operator operator, Logical logical) {
        return (T) addSpecification(new SpecificationUnit(name, operator, value, logical));
    }

    private T addSpecification(LogicalSpecification spec) {
        currentBind().add(spec);
        return (T) this;
    }

    protected Bind currentBind() {
        if (queryStack.isEmpty()) {
            if (autoStart) {
                start();
            } else {
                throw new IllegalStateException("query not start, please invoke start method befor equery or set autoStart=true");
            }
        }
        return queryStack.peek();
    }

    protected void prepareQuery() {
        finishQuery();
        if (specification == null && !allowEmptyCondition) {
            throw new IllegalStateException("query condition is null");
        }
    }

    protected CriteriaQuery createCriteriaQuery() {
        CriteriaQuery query = builder.createQuery();
        applySpecilicationToCriteriaQuery(query, specification);
        return query;
    }

    protected void applySpecilicationToCriteriaQuery(CriteriaQuery query, Specification specification2) {
    }

    private void finishQuery() {
        if (!queryStack.isEmpty()) {
            if (!autoFinish) {
                throw new IllegalStateException("query not finish, please invoke end method or set autoFinish=true");
            }
            for (int i = 0, max = queryStack.size(); i < max; i++) {
                end();
            }
        }
    }

    // enclose method

    public T start() {
        return start(Logical.AND);
    }

    public T start(Logical logical) {
        queryStack.push(new Bind(logical));
        return (T) this;
    }

    public T end() {
        Bind bind = queryStack.pop();
        if (!bind.isEmpty()) {
            if (bind.isAnd()) {
                this.specification = Specifications.where(specification).and(bind);
            } else {
                this.specification = Specifications.where(specification).or(bind);
            }
        }
        return (T) this;
    }

    static final class SpecificationWrapper<T> implements LogicalSpecification<T>, Serializable {

        private static final long serialVersionUID = 1L;
        private Specification spec;
        private Logical logical;

        public SpecificationWrapper(Specification spec, Logical logical) {
            this.spec = spec;
            this.logical = logical;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return spec.toPredicate(root, query, cb);
        }

        @Override
        public boolean isAnd() {
            return Logical.isAnd(logical);
        }

        @Override
        public boolean isOr() {
            return Logical.isOr(logical);
        }

    }

    static final class SpecificationUnit<T> implements Serializable, LogicalSpecification<T> {

        private static final long serialVersionUID = 1L;

        String name;
        Operator operator;
        Object value;
        Logical logical;

        public SpecificationUnit(String name, Operator operator, Object value, Logical logical) {
            this.name = name;
            this.operator = operator;
            this.value = value;
            this.logical = logical;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Expression<Object> x = QueryUtils.toExpressionRecursively(root, name);
            return operator.explain(x, cb, value);
        }

        @Override
        public boolean isAnd() {
            return Logical.isAnd(logical);
        }

        @Override
        public boolean isOr() {
            return Logical.isOr(logical);
        }

        @Override
        public String toString() {
            return logical + " " + name + " " + operator.symbol() + " " + "?";
        }

    }
}

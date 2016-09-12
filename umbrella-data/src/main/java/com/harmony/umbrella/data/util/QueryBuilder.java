package com.harmony.umbrella.data.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.CompositionType;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageRequest;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.domain.Sort.Direction;
import com.harmony.umbrella.data.domain.Sort.Order;
import com.harmony.umbrella.data.support.Bind;
import com.harmony.umbrella.data.support.CompositionSpecification;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBuilder<T extends QueryBuilder<T, M>, M> implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Stack<Bind> queryStack = new Stack<Bind>();
    private transient List<CompositionSpecification> temp = new ArrayList<CompositionSpecification>();

    protected EntityManager entityManager;
    protected CriteriaBuilder builder;
    protected boolean autoStart = true;
    protected boolean autoFinish = true;
    protected Class<M> entityClass;
    protected Sort sort;
    protected int pageNumber;
    protected int pageSize;
    protected boolean distinct;
    protected FetchAttributes fetchAttributes;
    protected JoinAttributes joinAttributes;
    protected Specification specification;

    // query property

    public QueryBuilder() {
    }

    public QueryBuilder(EntityManager entityManager) {
        this(null, entityManager);
    }

    public QueryBuilder(Class<M> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    public T withEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return (T) this;
    }

    public T withEntityClass(Class<M> entityClass) {
        return from(entityClass);
    }

    public T withSpecification(Specification<M> specification) {
        this.specification = specification;
        return (T) this;
    }

    public T withPageable(Pageable pageable) {
        this.pageNumber = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.sort = pageable.getSort();
        return (T) this;
    }

    public T withSort(Sort sort) {
        this.sort = sort;
        return (T) this;
    }

    public T from(Class<M> entityClass) {
        this.entityClass = entityClass;
        return (T) this;
    }

    // bundle

    public QueryBundle<M> bundle() {
        finishQuery();
        final QueryBundleImpl<M> o = new QueryBundleImpl<M>();
        o.entityClass = entityClass;
        o.pageable = new PageRequest(pageNumber < 0 ? 0 : pageNumber, pageSize < 1 ? 20 : pageSize, sort);
        o.specification = specification;
        o.fetchAttributes = fetchAttributes;
        o.joinAttributes = joinAttributes;
        o.distinct = distinct;
        return o;
    }

    public T unbundle(QueryBundle<M> bundle) {
        queryStack.clear();
        temp.clear();
        this.entityClass = bundle.getEntityClass();
        this.specification = bundle.getSpecification();
        this.pageNumber = bundle.getPageNumber();
        this.pageSize = bundle.getPageSize();
        this.sort = bundle.getSort();
        this.fetchAttributes = bundle.getFetchAttributes();
        this.joinAttributes = bundle.getJoinAttributes();
        this.distinct = bundle.isDistinct();
        return (T) this;
    }

    // result

    public QueryResult<M> execute() {
        return new QueryResultImpl<M>(entityManager, bundle());
    }

    public M getSingleResult() {
        return execute().getSingleResult();
    }

    public M getFirstResult() {
        return execute().getFirstResult();
    }

    public List<M> getResultList() {
        return execute().getResultList();
    }

    public Page<M> getResultPage() {
        return execute().getResultPage();
    }

    public long getCountResult() {
        return execute().getCountResult();
    }

    // paging

    public T paging(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        return (T) this;
    }

    // sort

    public T asc(String... name) {
        return orderBy(Direction.ASC, name);
    }

    public T desc(String... name) {
        return orderBy(Direction.DESC, name);
    }

    public T orderBy(Direction dir, String... name) {
        Order[] orders = new Order[name.length];
        for (int i = 0, max = orders.length; i < max; i++) {
            orders[i] = new Order(dir, name[i]);
        }
        return (T) orderBy(orders);
    }

    public T orderBy(Order... order) {
        if (order.length > 0) {
            this.sort = (this.sort == null) ? new Sort(order) : this.sort.and(new Sort(order));
        }
        return (T) this;
    }

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
        return start(CompositionType.OR)//
                .and(name, left, Operator.GREATER_THAN_OR_EQUAL)//
                .and(name, right, Operator.LESS_THAN_OR_EQUAL)//
                .end();
    }

    public T orNotBetween(String name, Object left, Object right) {
        return start(CompositionType.OR)//
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
        return addCondition(name, value, operator, CompositionType.AND);
    }

    public T or(String name, Object value, Operator operator) {
        return addCondition(name, value, operator, CompositionType.OR);
    }

    public T and(Specification spec) {
        return addSpecification(spec, CompositionType.AND);
    }

    public T or(Specification spec) {
        return addSpecification(spec, CompositionType.OR);
    }

    protected T addCondition(String name, Object value, Operator operator, CompositionType compositionType) {
        return (T) addSpecification(new SpecificationUnit(name, operator, value), compositionType);
    }

    private T addSpecification(Specification spec, CompositionType compositionType) {
        currentBind().add(spec, compositionType);
        return (T) this;
    }

    protected Bind currentBind() {
        if (queryStack.isEmpty()) {
            if (autoStart) {
                start();
            } else {
                throw new IllegalStateException("query not start, please invoke start method befor query or set autoStart=true");
            }
        }
        return queryStack.peek();
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

    // function

    public T distinct() {
        return distinct(true);
    }

    public T notDistinct() {
        return distinct(false);
    }

    public T distinct(boolean distinct) {
        this.distinct = distinct;
        return (T) this;
    }

    public T fetch(String... names) {
        return (T) fetch(JoinType.INNER, names);
    }

    public T fetch(JoinType joinType, String... names) {
        if (this.fetchAttributes == null) {
            this.fetchAttributes = new FetchAttributes();
        }
        for (String name : names) {
            this.fetchAttributes.attrs.add(new Attribute(name, joinType));
        }
        return (T) this;
    }

    public T join(String... names) {
        return (T) join(JoinType.INNER, names);
    }

    public T join(JoinType joinType, String... names) {
        if (this.joinAttributes == null) {
            this.joinAttributes = new JoinAttributes();
        }
        for (String name : names) {
            this.joinAttributes.attrs.add(new Attribute(name, joinType));
        }
        return (T) this;
    }

    // enclose method

    public T start() {
        return start(CompositionType.AND);
    }

    public T start(CompositionType compositionType) {
        queryStack.push(new Bind(compositionType));
        return (T) this;
    }

    public T end() {
        Bind bind = queryStack.pop();
        if (!bind.isEmpty()) {
            temp.add(bind);
        }
        if (queryStack.isEmpty()) {
            CompositionSpecification[] css = temp.toArray(new CompositionSpecification[0]);
            temp.clear();
            for (int i = css.length; i > 0; i--) {
                CompositionSpecification cs = css[i - 1];
                specification = cs.getCompositionType().combine(specification, cs);
            }
        }
        return (T) this;
    }

    static final class SpecificationUnit<T> implements Serializable, Specification<T> {

        private static final long serialVersionUID = 1L;

        String name;
        Operator operator;
        Object value;

        public SpecificationUnit(String name, Operator operator, Object value) {
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Expression<Object> x = QueryUtils.toExpressionRecursively(root, name);
            return operator.explain(x, cb, value);
        }

        @Override
        public String toString() {
            return name + " " + operator.symbol() + " " + "?";
        }

    }

    static final class FetchAttributes implements Serializable {

        private static final long serialVersionUID = 1L;

        List<Attribute> attrs = new ArrayList<Attribute>();

        public List<Attribute> getAttributes() {
            return Collections.unmodifiableList(attrs);
        }
    }

    static final class JoinAttributes implements Serializable {

        private static final long serialVersionUID = 1L;

        List<Attribute> attrs = new ArrayList<Attribute>();

        public List<Attribute> getAttributes() {
            return Collections.unmodifiableList(attrs);
        }

    }

    static final class Attribute implements Serializable {

        private static final long serialVersionUID = 1L;

        String name;
        JoinType joniType;

        public Attribute(String name, JoinType joniType) {
            this.name = name;
            this.joniType = joniType;
        }

        public String getName() {
            return name;
        }

        public JoinType getJoniType() {
            return joniType;
        }
    }

}

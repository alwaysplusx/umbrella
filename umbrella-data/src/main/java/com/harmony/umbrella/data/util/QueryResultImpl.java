package com.harmony.umbrella.data.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageImpl;
import com.harmony.umbrella.data.domain.PageRequest;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.util.QueryBuilder.Attribute;
import com.harmony.umbrella.data.util.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.util.QueryBuilder.JoinAttributes;
import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class QueryResultImpl<T> implements QueryResult<T> {

    private EntityManager entityManager;
    private CriteriaBuilder builder;

    private QueryBundle<T> bundle;

    protected QueryResultImpl(EntityManager entityManager, QueryBundle<T> bundle) {
        this.entityManager = entityManager;
        this.builder = entityManager.getCriteriaBuilder();
        this.bundle = bundle;
    }

    @Override
    public <E> E getColumnSingleResult(String column) {
        return (E) getColumnResultList(column, null);
    }

    @Override
    public <E> E getColumnSingleResult(String column, Class<E> columnType) {
        CriteriaQuery<E> query = buildColumnCriteriaQuery(columnType, column);
        try {
            return (E) entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <E> List<E> getColumnResultList(String column) {
        return getColumnResultList(column, null);
    }

    @Override
    public <E> List<E> getColumnResultList(String column, Class<E> columnType) {
        CriteriaQuery<E> query = buildColumnCriteriaQuery(columnType, column);
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <E> E getFunctionResult(String function, String column) {
        return getFunctionResult(function, column, null);
    }

    @Override
    public <E> E getFunctionResult(String function, String column, Class<E> functionResultType) {
        CriteriaQuery<E> query = buildFunctionCriteriaQuery(functionResultType, function, column);
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public <VO> VO getVoResult(String[] columns, Class<VO> voType) {
        CriteriaQuery<VO> query = buildColumnCriteriaQuery(voType, columns);
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <VO> List<VO> getVoResultList(String[] columns, Class<VO> voType) {
        CriteriaQuery<VO> query = buildColumnCriteriaQuery(voType, columns);
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public T getSingleResult() {
        CriteriaQuery<T> query = buildCriteriaQuery();
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public T getFirstResult() {
        List<T> result = getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<T> getResultList() {
        CriteriaQuery<T> query = buildCriteriaQuery();
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Page<T> getResultPage() {
        if (bundle.getPageable() == null) {
            throw new IllegalStateException("page request not set");
        }
        final int offset = bundle.getPageable().getOffset();
        final int size = bundle.getPageable().getPageSize();

        CriteriaQuery<T> query = buildCriteriaQuery();

        // page result
        long total = getCountResult();
        List<T> content = entityManager.createQuery(query).setFirstResult(offset).setMaxResults(size).getResultList();

        return new PageImpl<T>(content, bundle.getPageable(), total);
    }

    @Override
    public Page<T> getResultPage(int pageNumber, int pageSize) {
        return getResultPage(new PageRequest(pageNumber, pageSize, bundle.getSort()));
    }

    @Override
    public Page<T> getResultPage(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalStateException("page request not set");
        }
        CriteriaQuery<T> query = buildCriteriaQuery(bundle.getEntityClass(), pageable.getSort(), bundle.getFetchAttributes(), bundle.getJoinAttributes());
        // page count result
        long total = getCountResult();

        List<T> content = entityManager.createQuery(query)//
                .setFirstResult(pageable.getOffset())//
                .setMaxResults(pageable.getPageSize())//
                .getResultList();
        return new PageImpl<T>(content, pageable, total);
    }

    @Override
    public long getCountResult() {
        CriteriaQuery<Long> query = buildCriteriaQuery(Long.class, null, null, null);
        Root root = query.from(bundle.getEntityClass());
        if (bundle.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }
        return entityManager.createQuery(query).getSingleResult();
    }

    // query result

    private CriteriaQuery<T> buildCriteriaQuery() {
        return buildCriteriaQuery(bundle.getEntityClass(), bundle.getSort(), bundle.getFetchAttributes(), bundle.getJoinAttributes());
    }

    protected <E> CriteriaQuery<E> buildCriteriaQuery(Class<E> resultType, Sort sort, FetchAttributes fetchAttr, JoinAttributes joinAttr) {
        final CriteriaQuery<E> query = createQuery(resultType);
        Root root = query.from(bundle.getEntityClass());

        applySpecification(root, query, bundle.getSpecification());
        applySort(root, query, sort);
        applyFetchAttributes(root, fetchAttr);
        applyJoinAttribute(root, joinAttr);

        return query;
    }

    protected <E> CriteriaQuery<E> buildColumnCriteriaQuery(Class<E> resultType, String... column) {
        Assert.notEmpty(column, "query columns not allow empty");
        final CriteriaQuery<E> query = createQuery(resultType);
        Root root = query.from(bundle.getEntityClass());

        applySpecification(root, query, bundle.getSpecification());
        applySort(root, query, bundle.getSort());

        List<Selection> columns = new ArrayList<Selection>();
        for (String c : column) {
            columns.add(QueryUtils.toExpressionRecursively(root, c));
        }
        return columns.size() == 1 ? query.select(columns.get(0)) : query.multiselect(columns.toArray(new Selection[0]));
    }

    protected <E> CriteriaQuery<E> buildFunctionCriteriaQuery(Class<E> resultType, String function, String column) {
        final CriteriaQuery<E> query = createQuery(resultType);
        Root root = query.from(bundle.getEntityClass());

        applySpecification(root, query, bundle.getSpecification());

        Expression expression = QueryUtils.toExpressionRecursively(root, column);
        return query.select(builder.function(function, resultType, expression));
    }

    protected final <E> CriteriaQuery<E> createQuery(Class<E> resultType) {
        return (resultType == null || resultType == Object.class) ? (CriteriaQuery<E>) builder.createQuery() : builder.createQuery(resultType);
    }

    // apply query feature

    protected void applySpecification(Root root, CriteriaQuery query, Specification spec) {
        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, builder);
            query.where(predicate);
        }
    }

    protected void applySort(Root root, CriteriaQuery query, Sort sort) {
        if (sort != null) {
            query.orderBy(QueryUtils.toJpaOrders(sort, root, builder));
        }
    }

    protected void applyFetchAttributes(Root root, FetchAttributes attributes) {
        if (attributes != null && !attributes.attrs.isEmpty()) {
            for (Attribute attr : attributes.attrs) {
                root.fetch(attr.name, attr.joniType == null ? JoinType.INNER : attr.joniType);
            }
        }
    }

    protected void applyJoinAttribute(Root root, JoinAttributes attributes) {
        if (attributes != null && !attributes.attrs.isEmpty()) {
            for (Attribute attr : attributes.attrs) {
                root.join(attr.name, attr.joniType == null ? JoinType.INNER : attr.joniType);
            }
        }
    }

}

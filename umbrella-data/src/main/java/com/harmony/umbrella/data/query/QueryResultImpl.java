package com.harmony.umbrella.data.query;

import java.util.ArrayList;
import java.util.Collection;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import com.harmony.umbrella.data.QueryFeature;
import com.harmony.umbrella.data.query.QueryBuilder.Attribute;
import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;
import com.harmony.umbrella.data.util.QueryUtils;

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
        checkListQuery();
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
        checkListQuery();
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
        checkListQuery();
        CriteriaQuery<T> query = buildCriteriaQuery();
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Page<T> getResultPage() {
        return getResultPage(bundle.getPageable());
    }

    @Override
    public Page<T> getResultPage(int pageNumber, int pageSize) {
        return getResultPage(new PageRequest(pageNumber, pageSize, getSort()));
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
        if (QueryFeature.isEnabled(bundle.getQueryFeature(), QueryFeature.DISTINCT)) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }
        return entityManager.createQuery(query).getSingleResult();
    }

    // query result

    // function expression

    private void checkListQuery() {
        Specification spec = bundle.getSpecification();
        if (spec == null && !QueryFeature.ALLOW_LIST_QUERY_WHEN_EMPTY_CONDITION.isEnable(bundle.getQueryFeature())) {
            throw new IllegalStateException("not allow empty condition query list");
        }
    }

    private CriteriaQuery<T> buildCriteriaQuery() {
        return buildCriteriaQuery(bundle.getEntityClass(), getSort(), bundle.getFetchAttributes(), bundle.getJoinAttributes());
    }

    private Sort getSort() {
        return bundle.getPageable() == null ? null : bundle.getPageable().getSort();
    }

    protected <E> CriteriaQuery<E> buildCriteriaQuery(Class<E> resultType, Sort sort, FetchAttributes fetchAttr, JoinAttributes joinAttr) {
        final CriteriaQuery<E> query = createQuery(resultType);
        if (QueryFeature.isEnabled(bundle.getQueryFeature(), QueryFeature.DISTINCT)) {
            query.distinct(true);
        }
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
        applySort(root, query, getSort());

        List<Selection> columns = new ArrayList<Selection>();
        for (String c : column) {
            if (isFunctionExpression(c)) {
                columns.add(functionExpression(c, root));
            } else {
                columns.add(QueryUtils.toExpressionRecursively(root, c));
            }
        }
        return columns.size() == 1 ? query.select(columns.get(0)) : query.multiselect(columns.toArray(new Selection[0]));
    }

    protected Expression functionExpression(String attributeName, Root root) {
        int left = attributeName.indexOf("(");
        int right = attributeName.indexOf(")");
        String functionName = attributeName.substring(0, left);
        String expressionName = attributeName.substring(left + 1, right);
        return builder.function(functionName, null, root.get(expressionName));
    }

    protected boolean isFunctionExpression(String attributeName) {
        return attributeName.indexOf("(") > -1 && attributeName.indexOf(")") > -1;
    }

    protected <E> CriteriaQuery<E> buildFunctionCriteriaQuery(Class<E> resultType, String function, String column) {
        final CriteriaQuery<E> query = createQuery(resultType);
        Root root = query.from(bundle.getEntityClass());

        applySpecification(root, query, bundle.getSpecification());

        Expression expression = QueryUtils.toExpressionRecursively(root, column);
        return query.select(builder.function(function, resultType, expression));
    }

    protected final <E> CriteriaQuery<E> createQuery(Class<E> resultType) {
        CriteriaQuery<E> query = null;
        if (resultType == null || resultType == Object.class) {
            query = (CriteriaQuery<E>) builder.createQuery();
        } else {
            query = builder.createQuery(resultType);
        }
        return query.distinct(QueryFeature.DISTINCT.isEnable(bundle.getQueryFeature()));
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
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
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

    protected void applyGrouping(Root root, CriteriaQuery query, Collection<String> grouping) {
        for (String s : grouping) {
            query.groupBy(QueryUtils.toExpressionRecursively(root, s));
        }
    }

}

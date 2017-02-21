package com.harmony.umbrella.data.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.harmony.umbrella.data.QueryFeature;

/**
 * @author wuxii@foxmail.com
 */
public class QueryResultImpl<T> implements QueryResult<T> {

    protected final EntityManager entityManager;
    protected final CriteriaBuilder builder;
    protected final QueryBundle<T> bundle;

    protected QueryResultImpl(EntityManager entityManager, QueryBundle<T> bundle) {
        this.entityManager = entityManager;
        this.builder = entityManager.getCriteriaBuilder();
        this.bundle = bundle;
    }

    @Override
    public <E> E getColumnSingleResult(String column) {
        return (E) getColumnSingleResult(column, null);
    }

    @Override
    public <E> E getColumnSingleResult(String column, Class<E> columnType) {
        CriteriaQuery<E> query = buildColumnCriteriaQuery(new String[] { column }, columnType);
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
        CriteriaQuery<E> query = buildColumnCriteriaQuery(new String[] { column }, columnType);
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
        CriteriaQuery<VO> query = buildColumnCriteriaQuery(columns, voType);
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <VO> List<VO> getVoResultList(String[] columns, Class<VO> voType) {
        CriteriaQuery<VO> query = buildColumnCriteriaQuery(columns, voType);
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public T getSingleResult() {
        CriteriaQuery<T> query = createQuery().assembly();
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public T getFirstResult() {
        CriteriaQuery<T> query = createQuery().assembly();
        try {
            return entityManager.createQuery(query)//
                    .setFirstResult(0)//
                    .setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<T> getAllMatchResult() {
        CriteriaQuery<T> query = createQuery().assembly();
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<T> getResultList() {
        return getResultList(bundle.getPageable());
    }

    @Override
    public List<T> getResultList(int pageNumber, int pageSize) {
        return getResultList(new PageRequest(pageNumber, pageSize));
    }

    @Override
    public List<T> getResultList(Pageable pageable) {
        InternalQuery<T, T> query = createQuery(bundle.getEntityClass());
        query.applyFetchAttributes();
        query.applyGrouping();
        query.applyJoinAttribute();
        query.applySpecification();
        return null;
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
        // page count result
        long total = getCountResult();

        CriteriaQuery<T> query = buildCriteriaQuery(bundle.getEntityClass(), pageable.getSort(), bundle.getFetchAttributes(), bundle.getJoinAttributes());

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

    private InternalQuery<T, T> createQuery() {
        return createQuery(bundle.getEntityClass());
    }

    protected <R> InternalQuery<T, R> createQuery(Class<R> resultClass) {
        return new InternalQuery<T, R>(resultClass, builder, bundle);
    }

    // function expression

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

    // query method

}

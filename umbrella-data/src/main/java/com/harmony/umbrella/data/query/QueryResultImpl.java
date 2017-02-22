package com.harmony.umbrella.data.query;

import static com.harmony.umbrella.data.query.InternalQuery.Assembly.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        InternalQuery<T, E> query = createQuery(columnType);
        query.assembly(FETCH, JOIN, GROUP);
        query.select(column);
        try {
            return (E) entityManager.createQuery(query.query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <E> List<E> getColumnResultList(String column) {
        return getColumnResultList(column, null);
    }

    @Override
    public <E> List<E> getColumnResultList(String column, Class<E> resultType) {
        InternalQuery<T, E> query = createQuery(resultType);
        query.assembly(FETCH, JOIN, GROUP, SORT);
        query.select(column);
        TypedQuery<E> typedQuery = entityManager.createQuery(query.query);
        if (query.applyPaging(typedQuery) && !query.hasRestriction() && !query.isAllowFullTableQuery()) {
            throw new IllegalStateException("not allow empty condition full table query");
        }
        return typedQuery.getResultList();
    }

    @Override
    public <E> E getFunctionResult(String function, String column) {
        return getFunctionResult(function, column, null);
    }

    @Override
    public <E> E getFunctionResult(String function, String column, Class<E> resultType) {
        InternalQuery<T, E> query = createQuery(resultType);
        query.assembly(FETCH, JOIN, GROUP, SORT);
        query.selectFunction(function, column, resultType);
        return entityManager.createQuery(query.query).getSingleResult();
    }

    @Override
    public <VO> VO getVoSingleResult(String[] columns, Class<VO> resultType) {
        InternalQuery<T, VO> query = createQuery(resultType);
        query.assembly(FETCH, JOIN, GROUP);
        query.select(columns);
        try {
            return entityManager.createQuery(query.query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <VO> List<VO> getVoResultList(String[] columns, Class<VO> resultType) {
        InternalQuery<T, VO> query = createQuery(resultType);
        query.assembly(FETCH, JOIN, GROUP, SORT);
        query.select(columns);
        TypedQuery<VO> typedQuery = entityManager.createQuery(query.query);
        if (query.applyPaging(typedQuery) && !query.hasRestriction() && !query.isAllowFullTableQuery()) {
            throw new IllegalStateException("not allow empty condition full table query");
        }
        return typedQuery.getResultList();
    }

    @Override
    public T getSingleResult() {
        CriteriaQuery<T> query = createQuery().assembly(FETCH, JOIN);
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public T getFirstResult() {
        CriteriaQuery<T> query = createQuery().assembly(FETCH, JOIN, SORT);
        try {
            return entityManager.createQuery(query)//
                    .setFirstResult(0)//
                    .setMaxResults(1)//
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<T> getAllMatchResult() {
        InternalQuery<T, T> query = createQuery();
        query.assembly(FETCH, JOIN, SORT);
        if (!query.hasRestriction() && !query.isAllowFullTableQuery()) {
            throw new IllegalStateException("not allow empty condition full table query");
        }
        return entityManager.createQuery(query.query).getResultList();
    }

    @Override
    public List<T> getResultList() {
        InternalQuery<T, T> query = createQuery();
        query.assembly(FETCH, JOIN, SORT);
        TypedQuery<T> typedQuery = entityManager.createQuery(query.query);
        if (!query.applyPaging(typedQuery) && !query.hasRestriction() && !query.isAllowFullTableQuery()) {
            throw new IllegalStateException("not allow empty condition full table query");
        }
        return typedQuery.getResultList();
    }

    @Override
    public Page<T> getResultPage() {
        List<T> content = getResultList();
        PageRequest pageable = new PageRequest(bundle.getPageNumber(), bundle.getPageSize(), bundle.getSort());
        return new PageImpl<T>(content, pageable, getCountResult());
    }

    @Override
    public long getCountResult() {
        InternalQuery<T, Long> query = createQuery(Long.class);
        if (QueryFeature.DISTINCT.isEnable(bundle.getQueryFeature())) {
            query.query.select(builder.countDistinct(query.root));
        } else {
            query.query.select(builder.count(query.root));
        }
        return entityManager.createQuery(query.query).getSingleResult();
    }

    // build query 

    private InternalQuery<T, T> createQuery() {
        return createQuery(bundle.getEntityClass());
    }

    protected <R> InternalQuery<T, R> createQuery(Class<R> resultClass) {
        return new InternalQuery<T, R>(resultClass, builder, bundle);
    }

}

package com.harmony.umbrella.data.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryResult;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleQueryableJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements QueryableRepository<T, ID> {

    private Class<T> domainClass;
    private EntityManager entityManager;

    public SimpleQueryableJpaRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
        this.domainClass = domainClass;
    }

    protected <E> JpaQueryBuilder<E> queryWith(Class<E> entityClass) {
        return new JpaQueryBuilder<E>(entityManager).from(entityClass);
    }

    @Override
    public QueryResult<T> query(QueryBundle<T> bundle) {
        return queryWith(domainClass).unbundle(bundle).execute();
    }

    @Override
    public <M> QueryResult<M> query(QueryBundle<?> bundle, Class<M> entityClass) {
        return queryWith(entityClass).unbundle((QueryBundle) bundle).from(entityClass).execute();
    }

    @Override
    public T getSingleResult(QueryBundle<T> bundle) {
        return query(bundle).getSingleResult();
    }

    @Override
    public T getFirstResult(QueryBundle<T> bundle) {
        return query(bundle).getFirstResult();
    }

    @Override
    public List<T> getResultList(QueryBundle<T> bundle) {
        return query(bundle).getResultList();
    }

    @Override
    public Page<T> getResultPage(QueryBundle<T> bundle) {
        return query(bundle).getResultPage();
    }

    @Override
    public <RESULT> RESULT query(QueryBundle<T> bundle, QueryResultFetcher<T, RESULT> fetcher) {
        return fetcher.fetch(query(bundle));
    }

    @Override
    public <M, RESULT> RESULT query(QueryBundle<?> bundle, Class<M> entityClass, QueryResultFetcher<M, RESULT> fetcher) {
        return fetcher.fetch(query(bundle, entityClass));
    }

}

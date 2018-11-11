package com.harmony.umbrella.data.repository;

import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleQueryableJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements QueryableRepository<T, ID> {

    private Class<T> domainClass;
    private EntityManager entityManager;

    public SimpleQueryableJpaRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.domainClass = domainClass;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Class<T> getDomainClass() {
        return domainClass;
    }

    protected <E> JpaQueryBuilder<E> queryWith(Class<E> entityClass) {
        return JpaQueryBuilder.<E>newBuilder().withEntityManager(entityManager).from(entityClass);
    }

    @Override
    public QueryResult<T> query(QueryBundle<T> bundle) {
        return query(bundle, domainClass);
    }

    @Override
    public <M> QueryResult<M> query(QueryBundle<?> bundle, Class<M> domainClass) {
        return queryWith(domainClass).unbundle((QueryBundle) bundle).from(domainClass).execute();
    }

    @Override
    public Optional<T> getSingleResult(QueryBundle<T> bundle) {
        return query(bundle).getSingleResult();
    }

    @Override
    public Optional<T> getFirstResult(QueryBundle<T> bundle) {
        return query(bundle).getFirstResult();
    }

    @Override
    public List<T> getResultList(QueryBundle<T> bundle) {
        return query(bundle).getResultList();
    }

    @Override
    public List<T> getAllResult(QueryBundle<T> bundle) {
        return query(bundle).getAllResult();
    }

    @Override
    public Page<T> getPageResult(QueryBundle<T> bundle) {
        return query(bundle).getResultPage();
    }

    @Override
    public long countResult(QueryBundle<T> bundle) {
        return query(bundle).count();
    }

}

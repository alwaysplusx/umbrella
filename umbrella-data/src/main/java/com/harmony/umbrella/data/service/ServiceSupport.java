package com.harmony.umbrella.data.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.repository.QueryableRepository;

/**
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ServiceSupport<T, ID extends Serializable> implements Service<T, ID> {

    protected abstract QueryableRepository<T, ID> getRepository();

    @Override
    public T saveOrUpdate(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public void delete(T entity) {
        getRepository().delete(entity);
    }

    @Override
    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    @Override
    public T getAndDelete(ID id) {
        T entity = findById(id);
        getRepository().delete(entity);
        return entity;
    }

    @Override
    public T findOne(QueryBundle<T> bundle) {
        return getRepository().getSingleResult(bundle);
    }

    @Override
    public T findById(ID id) {
        Optional<T> o = getRepository().findById(id);
        return o.isPresent() ? o.get() : null;
    }

    @Override
    public T findFirst(QueryBundle<T> bundle) {
        return getRepository().getFirstResult(bundle);
    }

    @Override
    public List<T> findList(QueryBundle<T> bundle) {
        return getRepository().getResultList(bundle);
    }

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public Page<T> findPage(QueryBundle<T> bundle) {
        return getRepository().getPageResult(bundle);
    }

    @Override
    public boolean exists(QueryBundle<T> bundle) {
        return count(bundle) > 0;
    }

    @Override
    public long count(QueryBundle<T> bundle) {
        return getRepository().countResult(bundle);
    }

    @Override
    public void deleteInBatch(List<T> entities) {
        getRepository().deleteInBatch(entities);
    }

    @Override
    public void deleteAll() {
        getRepository().deleteAllInBatch();
    }

    protected JpaQueryBuilder<T> queryWith() {
        return queryWith((Class<T>) null).withEntityManager(getRepository().getEntityManager());
    }

    protected <M> JpaQueryBuilder<M> queryWith(Class<M> domainClass) {
        return JpaQueryBuilder.<M> newBuilder().from(domainClass).withEntityManager(getRepository().getEntityManager());
    }

}

package com.harmony.umbrella.data.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.repository.QueryableRepository;

/**
 * FIXME 补充完成
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
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public T update(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public void delete(T entity) {
        getRepository().delete(entity);
    }

    @Override
    public void delete(ID id) {
        getRepository().delete(id);
    }

    @Override
    public T getAndDelete(ID id) {
        T entity = findOne(id);
        getRepository().delete(entity);
        return entity;
    }

    @Override
    public T findOne(QueryBundle<T> bundle) {
        return getRepository().getSingleResult(bundle);
    }

    @Override
    public T findOne(ID id) {
        return getRepository().findOne(id);
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
        return getRepository().getResultPage(bundle);
    }

    @Override
    public boolean exists(QueryBundle<T> bundle) {
        return count(bundle) > 0;
    }

    @Override
    public long count(QueryBundle<T> bundle) {
        return getRepository().getCountResult(bundle);
    }

    @Override
    public void deleteInBatch(List<T> entities) {
        getRepository().deleteInBatch(entities);
    }

    @Override
    public void deleteAll() {
        getRepository().deleteAllInBatch();
    }

}

package com.harmony.umbrella.data.service;

import com.harmony.umbrella.data.JpaQueryBuilder;
import com.harmony.umbrella.data.QueryBundle;
import com.harmony.umbrella.data.repository.QueryableRepository;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ServiceSupport<T, ID extends Serializable> implements Service<T, ID> {

    protected abstract QueryableRepository<T, ID> getRepository();

    protected abstract Class<T> getDomainClass();

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
        // TODO update optional
        return null;
    }

    @Override
    public T findOne(QueryBundle<T> bundle) {
        return getRepository().getSingleResult(bundle);
    }

    @Override
    public T findById(ID id) {
        return getRepository().findById(id).orElse(null);
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
        return queryWith(getDomainClass());
    }

    protected <M> JpaQueryBuilder<M> queryWith(Class<M> domainClass) {
        return JpaQueryBuilder.<M>newBuilder().from(domainClass).withEntityManager(getRepository().getEntityManager());
    }

}

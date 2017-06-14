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
public abstract class ServiceSupport<T, ID extends Serializable> implements Service<T> {

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
    public T findOne(QueryBundle<T> bundle) {
        return null;
    }

    @Override
    public T findFirst(QueryBundle<T> bundle) {
        return null;
    }

    @Override
    public List<T> findList(QueryBundle<T> bundle) {
        return null;
    }

    @Override
    public Page<T> findPage(QueryBundle<T> bundle) {
        return null;
    }

}

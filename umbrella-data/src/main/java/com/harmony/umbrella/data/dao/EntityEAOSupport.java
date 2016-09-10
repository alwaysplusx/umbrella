package com.harmony.umbrella.data.dao;

import java.util.List;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;

/**
 * @author wuxii@foxmail.com
 */
public abstract class EntityEAOSupport<T> extends JpaDAOSupport implements EntityDAO<T> {

    protected final Class<T> entityClass;

    protected EntityEAOSupport(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public int remove(Specification<T> spec) {
        return remove(entityClass, spec);
    }

    @Override
    public T removeById(Object ID) {
        return remove(entityClass, ID);
    }

    @Override
    public List<T> removeById(Object... ID) {
        return remove(entityClass, ID);
    }

    @Override
    public T findById(Object ID) {
        return findOne(entityClass, ID);
    }

    @Override
    public T findOne(Specification<T> spec) {
        return findOne(entityClass, spec);
    }

    @Override
    public List<T> findAll() {
        return findAll(entityClass);
    }

    @Override
    public List<T> findAll(Sort sort) {
        return findAll(entityClass, sort);
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        return findAll(entityClass, spec);
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort) {
        return findAll(entityClass, spec, sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable, Specification<T> spec) {
        return findAll(entityClass, pageable, spec);
    }

    @Override
    public long countAll() {
        return countAll(entityClass);
    }

    @Override
    public long count(Specification<T> spec) {
        return count(entityClass, spec);
    }

}

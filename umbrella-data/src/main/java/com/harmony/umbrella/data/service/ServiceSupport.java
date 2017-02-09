package com.harmony.umbrella.data.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.harmony.umbrella.data.Persistable;
import com.harmony.umbrella.data.dao.JpaDAO;
import com.harmony.umbrella.data.query.QueryBundle;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ServiceSupport<T extends Persistable<ID>, ID extends Serializable> implements Service<T, ID> {

    protected final Class<T> entityClass;

    protected ServiceSupport(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract JpaDAO getJpaDAO();

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    protected EntityManager getEntityManager() {
        return getJpaDAO().getEntityManager();
    }

    @Override
    public T persist(T entity) {
        return getJpaDAO().persist(entity);
    }

    @Override
    public T merge(T entity) {
        return getJpaDAO().merge(entity);
    }

    @Override
    public void remove(T entity) {
        getJpaDAO().remove(entity);
    }

    @Override
    public T removeById(Object ID) {
        return getJpaDAO().remove(entityClass, ID);
    }

    @Override
    public List<T> removeById(List<ID> ID) {
        List<T> result = new ArrayList<T>();
        JpaDAO jpaDAO = getJpaDAO();
        for (ID id : ID) {
            result.add(jpaDAO.remove(entityClass, id));
        }
        return result;
    }

    @Override
    public T findById(Object ID) {
        return getJpaDAO().findOne(entityClass, ID);
    }

    @Override
    public T findOne(final QueryBundle<T> bundle) {
        return getJpaDAO().query(bundle, entityClass).getSingleResult();
    }

    @Override
    public List<T> findAll() {
        return getJpaDAO().findAll(entityClass);
    }

    @Override
    public List<T> findAll(Sort sort) {
        return getJpaDAO().findAll(entityClass, sort);
    }

    @Override
    public List<T> findAll(List<ID> ID) {
        List<T> result = new ArrayList<T>();
        JpaDAO jpaDAO = getJpaDAO();
        for (ID id : ID) {
            result.add(jpaDAO.findOne(entityClass, id));
        }
        return result;
    }

    @Override
    public List<T> findAll(QueryBundle<T> bundle) {
        return getJpaDAO().query(bundle, entityClass).getResultList();
    }

    @Override
    public Page<T> findPage(QueryBundle<T> bundle) {
        return getJpaDAO().query(bundle, entityClass).getResultPage();
    }

    @Override
    public boolean exists(ID ID) {
        return findById(ID) != null;
    }

    @Override
    public long count() {
        return getJpaDAO().countAll(entityClass);
    }

    @Override
    public long count(QueryBundle<T> bundle) {
        verifyBundle(bundle);
        return getJpaDAO().query(bundle, entityClass).getCountResult();
    }

    private void verifyBundle(QueryBundle bundle) {
        Class<T> entityClass = getEntityClass();
        if (bundle.getEntityClass() != null && !bundle.getEntityClass().equals(entityClass)) {
            throw new IllegalArgumentException("entity type not match, logic entity type " + entityClass + ", bundle entity type " + bundle.getEntityClass());
        }
    }

}

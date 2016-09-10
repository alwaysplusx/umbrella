package com.harmony.umbrella.data.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.Persistable;
import com.harmony.umbrella.data.dao.JpaDAO;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.util.JpaQueryBuilder;
import com.harmony.umbrella.data.util.QueryBundle;

/**
 * @author wuxii@foxmail.com
 */
public class ServiceSupport<T extends Persistable<ID>, ID extends Serializable> implements Service<T, ID> {

    protected final Class<T> entityClass;

    protected ServiceSupport(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected JpaDAO getJpaDAO() {
        return null;
    }

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
    public T findOne(QueryBundle<T> bundle) {
        return query().unbundle(bundle).getSingleResult();
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
        return query(bundle).getResultList();
    }

    @Override
    public Page<T> findPage(QueryBundle<T> bundle) {
        return query(bundle).getResultPage();
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
        return query(bundle).count();
    }

    protected JpaQueryBuilder<T> query() {
        return query(getEntityClass());
    }

    protected JpaQueryBuilder<T> query(QueryBundle<T> bundle) {
        final Class<T> entityClass = bundle.getEntityClass();
        if (entityClass != null && this.entityClass.isAssignableFrom(entityClass)) {
            throw new IllegalArgumentException("entity type not match, logic entity type " + entityClass + ", bundle entity type " + bundle.getEntityClass());
        }
        return new JpaQueryBuilder<T>(getEntityManager()).unbundle(bundle).from(entityClass == null ? this.entityClass : entityClass);
    }

    protected JpaQueryBuilder<T> query(Pageable pageable) {
        return query().withPageable(pageable);
    }

    protected final <M> JpaQueryBuilder<M> query(Class<M> entityClass) {
        return new JpaQueryBuilder<M>(getEntityManager()).withEntityClass(entityClass);
    }
}

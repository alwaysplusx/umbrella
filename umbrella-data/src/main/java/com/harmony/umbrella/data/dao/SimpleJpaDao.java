package com.harmony.umbrella.data.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleJpaDao<E, ID extends Serializable> extends JpaDaoSupport<E, ID> {

    protected final Class<E> entityClass;

    protected final EntityManager em;

    public SimpleJpaDao(Class<E> entityClass, EntityManager entityManager) {
        this.em = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<E> getEntityClass() {
        return entityClass;
    }

}

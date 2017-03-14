package com.harmony.umbrella.data.dao;

import javax.persistence.EntityManager;

/**
 * @deprecated spring-data repository or
 *             {@linkplain com.harmony.umbrella.data.repository.QueryableRepository}
 * @author wuxii@foxmail.com
 */
public class SimpleEntityDAO<T> extends EntityDAOSupport<T> {

    private EntityManager entityManager;

    public SimpleEntityDAO(EntityManager em, Class<T> entityClass) {
        super(entityClass);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

}

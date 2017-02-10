package com.harmony.umbrella.data.support;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.dao.EntityDAOSupport;

/**
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

package com.harmony.umbrella.data.dao;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.jpa.provider.PersistenceProvider;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleDao extends DaoSupport {

    protected final EntityManager em;

    protected final PersistenceProvider provider;

    public SimpleDao(EntityManager entityManager) {
        this.em = entityManager;
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected PersistenceProvider getPersistenceProvider() {
        return provider;
    }
}

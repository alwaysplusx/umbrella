package com.harmony.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.harmony.umbrella.data.Dao;
import com.harmony.umbrella.data.dao.DaoSupport;

/**
 * @author wuxii@foxmail.com
 */
public class BaseDao extends DaoSupport implements Dao {

    @PersistenceContext(unitName = "")
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

}

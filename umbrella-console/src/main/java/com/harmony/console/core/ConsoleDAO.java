package com.harmony.console.core;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.harmony.umbrella.data.dao.JpaDAO;
import com.harmony.umbrella.data.dao.JpaDAOSupport;

/**
 * @author wuxii@foxmail.com
 */
@Repository
public class ConsoleDAO extends JpaDAOSupport implements JpaDAO {

    private EntityManager entityManager;

    @PersistenceContext(unitName = "umbrella-console")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

}

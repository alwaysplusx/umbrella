package com.harmony.umbrella.data.query;

import javax.persistence.EntityManager;

/**
 * @author wuxii@foxmail.com
 */
public class JpaQueryBuilder<M> extends QueryBuilder<JpaQueryBuilder<M>, M> {

    private static final long serialVersionUID = 1L;

    public JpaQueryBuilder() {
    }

    public JpaQueryBuilder(Class<M> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    public JpaQueryBuilder(EntityManager entityManager) {
        super(entityManager);
    }

}

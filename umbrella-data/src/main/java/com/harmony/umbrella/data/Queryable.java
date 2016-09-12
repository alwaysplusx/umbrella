package com.harmony.umbrella.data;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.util.JpaQueryBuilder;
import com.harmony.umbrella.data.util.QueryBundle;
import com.harmony.umbrella.data.util.QueryResult;

/**
 * @author wuxii@foxmail.com
 */
public interface Queryable {

    EntityManager getEntityManager();

    default <M> QueryResult<M> query(QueryBundle<M> bundle) {
        if (bundle.getEntityClass() == null) {
            throw new IllegalStateException("entity class not set");
        }
        return new JpaQueryBuilder<M>(getEntityManager()).unbundle(bundle).execute();
    }

    default <M> QueryResult<M> query(QueryBundle<M> bundle, Class<M> entityClass) {
        if (entityClass == null) {
            throw new IllegalStateException("entity class not set");
        }
        return new JpaQueryBuilder(getEntityManager()).unbundle(bundle).from(entityClass).execute();
    }

}

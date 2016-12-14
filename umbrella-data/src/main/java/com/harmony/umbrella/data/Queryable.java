package com.harmony.umbrella.data;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryResult;

/**
 * @author wuxii@foxmail.com
 */
public interface Queryable {

    EntityManager getEntityManager();

    <M> QueryResult<M> query(QueryBundle<M> bundle);

    <M> QueryResult<M> query(QueryBundle<M> bundle, Class<M> entityClass);

}

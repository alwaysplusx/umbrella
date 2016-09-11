package com.harmony.umbrella.data;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.util.JpaQueryBuilder;
import com.harmony.umbrella.data.util.QueryBundle;

/**
 * @author wuxii@foxmail.com
 */
public interface Queryable {

    EntityManager getEntityManager();

    <M> JpaQueryBuilder<M> query(QueryBundle<M> bundle);

}

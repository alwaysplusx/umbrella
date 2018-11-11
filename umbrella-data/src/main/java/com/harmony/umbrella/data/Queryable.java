package com.harmony.umbrella.data;

import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryResult;

/**
 * @author wuxii@foxmail.com
 */
public interface Queryable<T> {

    QueryResult<T> query(QueryBundle<T> bundle);

    <M> QueryResult<M> query(QueryBundle<?> bundle, Class<M> domainClass);

}

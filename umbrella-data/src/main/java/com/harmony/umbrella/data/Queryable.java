package com.harmony.umbrella.data;

/**
 * @author wuxii@foxmail.com
 */
public interface Queryable<T> {

    QueryResult<T> query(QueryBundle<T> bundle);

    <M> QueryResult<M> query(QueryBundle<?> bundle, Class<M> domainClass);

}

package com.harmony.umbrella.data;

import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryResult;

/**
 * @author wuxii@foxmail.com
 */
public interface Queryable<T> {

    QueryResult<T> query(QueryBundle<T> bundle);

    <M> QueryResult<M> query(QueryBundle<?> bundle, Class<M> entityClass);

    <RESULT> RESULT query(QueryBundle<T> bundle, QueryResultConverter<T, RESULT> converter);

    <M, RESULT> RESULT query(QueryBundle<?> bundle, Class<M> entityClass, QueryResultConverter<M, RESULT> converter);

    public interface QueryResultConverter<M, RESULT> {

        RESULT convert(QueryResult<M> result);

    }
}

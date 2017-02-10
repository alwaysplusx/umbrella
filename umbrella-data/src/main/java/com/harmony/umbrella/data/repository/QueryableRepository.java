package com.harmony.umbrella.data.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.harmony.umbrella.data.Queryable;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryResult;

/**
 * @author wuxii@foxmail.com
 */
@NoRepositoryBean
public interface QueryableRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, Queryable<T> {

    <RESULT> RESULT query(QueryBundle<T> bundle, QueryResultFetcher<RESULT> fetcher);

    T getSingleResult(QueryBundle<T> bundle);

    T getFirstResult(QueryBundle<T> bundle);

    List<T> getResultList(QueryBundle<T> bundle);

    Page<T> getResultPage(QueryBundle<T> bundle);

    public interface QueryResultFetcher<RESULT> {

        RESULT fetch(QueryResult<?> result);

    }

}

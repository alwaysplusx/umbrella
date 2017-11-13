package com.harmony.umbrella.data.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.harmony.umbrella.data.Queryable;
import com.harmony.umbrella.data.query.QueryBundle;

/**
 * @author wuxii@foxmail.com
 */
@NoRepositoryBean
public interface QueryableRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>, Queryable<T> {

    T getSingleResult(QueryBundle<T> bundle);

    T getFirstResult(QueryBundle<T> bundle);

    List<T> getResultList(QueryBundle<T> bundle);

    List<T> getRangeResout(QueryBundle<T> bundle);

    Page<T> getPageResult(QueryBundle<T> bundle);

    long countResult(QueryBundle<T> bundle);

}

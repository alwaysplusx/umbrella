package com.harmony.umbrella.data.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.harmony.umbrella.data.query.QueryBundle;

/**
 * @author wuxii@foxmail.com
 */
public interface Service<T> {

    T saveOrUpdate(T entity);

    void delete(T entity);

    T findOne(QueryBundle<T> bundle);

    T findFirst(QueryBundle<T> bundle);

    List<T> findList(QueryBundle<T> bundle);

    Page<T> findPage(QueryBundle<T> bundle);

}

package com.harmony.umbrella.data.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import com.harmony.umbrella.data.query.QueryBundle;

/**
 * @author wuxii@foxmail.com
 */
public interface Service<T, ID extends Serializable> {

    T saveOrUpdate(T entity);

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    void delete(ID id);

    T getAndDelete(ID id);

    T findOne(QueryBundle<T> bundle);

    T findOne(ID id);

    T findFirst(QueryBundle<T> bundle);

    List<T> findList(QueryBundle<T> bundle);

    List<T> findAll();

    Page<T> findPage(QueryBundle<T> bundle);

    boolean exists(QueryBundle<T> bundle);

    long count(QueryBundle<T> bundle);

    void deleteInBatch(List<T> entities);

    void deleteAll();

}

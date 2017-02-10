package com.harmony.umbrella.data.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;

import com.harmony.umbrella.data.query.QueryBundle;

/**
 * @author wuxii@foxmail.com
 */
public interface Service<T extends Persistable<ID>, ID extends Serializable> {

    T persist(T entity);

    T merge(T entity);

    void remove(T entity);

    T removeById(Object ID);

    List<T> removeById(List<ID> ID);

    T findById(Object ID);

    T findOne(QueryBundle<T> bundle);

    List<T> findAll();

    List<T> findAll(Sort sort);

    List<T> findAll(List<ID> ID);

    List<T> findAll(QueryBundle<T> bundle);

    Page<T> findPage(QueryBundle<T> bundle);

    boolean exists(ID ID);

    long count();

    long count(QueryBundle<T> bundle);

}

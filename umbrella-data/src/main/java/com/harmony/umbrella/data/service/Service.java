package com.harmony.umbrella.data.service;

import com.harmony.umbrella.data.QueryBundle;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author wuxii@foxmail.com
 */
public interface Service<T, ID extends Serializable> {

    T saveOrUpdate(T entity);

    void delete(T entity);

    void deleteById(ID id);

    Optional<T> getAndDelete(ID id);

    Optional<T> findOne(QueryBundle<T> bundle);

    Optional<T> findById(ID id);

    Optional<T> findFirst(QueryBundle<T> bundle);

    List<T> findList(QueryBundle<T> bundle);

    List<T> findAll();

    Page<T> findPage(QueryBundle<T> bundle);

    boolean exists(QueryBundle<T> bundle);

    long count(QueryBundle<T> bundle);

    void deleteInBatch(List<T> entities);

    void deleteAll();
}

package com.harmony.umbrella.data;

import java.io.Serializable;
import java.util.List;

import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageRequest;

/**
 * @author wuxii@foxmail.com
 */
public interface Business<T extends Model<ID>, ID extends Serializable> {

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    void delete(Iterable<T> entities);

    T deleteById(ID id);

    void deleteByIds(Iterable<ID> ids);

    int delete(Bond bond);

    T findOne(ID id);

    T findOne(Bond bond);

    List<T> findAll();

    List<T> findAll(Bond bond);

    List<T> findAll(Iterable<ID> ids);

    long count(Bond bond);

    long countAll();

    boolean exists(ID id);

    boolean exists(Bond bond);

    boolean isNew(T entity);

    Page<T> page(Bond bond, PageRequest pageRequest);

    Page<T> page(PageRequest pageRequest);
}

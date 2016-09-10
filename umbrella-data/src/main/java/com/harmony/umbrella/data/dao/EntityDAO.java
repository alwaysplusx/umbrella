package com.harmony.umbrella.data.dao;

import java.util.List;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;

public interface EntityDAO<T> extends JpaDAO, DAO {

    int remove(Specification<T> spec);

    T removeById(Object ID);

    List<T> removeById(Object... ID);

    T findById(Object ID);

    T findOne(Specification<T> spec);

    List<T> findAll();

    List<T> findAll(Sort sort);

    List<T> findAll(Specification<T> spec);

    List<T> findAll(Specification<T> spec, Sort sort);

    Page<T> findAll(Pageable pageable, Specification<T> spec);

    long countAll();

    long count(Specification<T> spec);

}
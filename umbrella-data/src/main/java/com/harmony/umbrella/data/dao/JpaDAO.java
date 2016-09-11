package com.harmony.umbrella.data.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.Queryable;
import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;

/**
 * @author wuxii@foxmail.com
 */
public interface JpaDAO extends DAO, Queryable {

    <T> int remove(Class<T> entityClass, Specification<T> spec);

    <T> T findOne(Class<T> entityClass, Specification<T> spec);

    <T> List<T> findAll(Class<T> entityClass, Specification<T> spec);

    <T> List<T> findAll(Class<T> entityClass, Specification<T> spec, Sort sort);

    <T> Page<T> findAll(Class<T> entityClass, Pageable pageable);

    <T> Page<T> findAll(Class<T> entityClass, Pageable pageable, Specification<T> spec);

    <T> long count(Class<T> entityClass, Specification<T> spec);

    // util method

    EntityManager getEntityManager();
}

package com.harmony.umbrella.data.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.Queryable;

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

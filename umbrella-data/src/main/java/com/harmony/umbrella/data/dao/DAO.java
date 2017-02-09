package com.harmony.umbrella.data.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;

/**
 * @author wuxii@foxmail.com
 */
public interface DAO {

    <T> T persist(T entity);

    <T> List<T> persist(T... entities);

    <T> T merge(T entity);

    <T> List<T> merge(T... entities);

    void remove(Object entity);

    void remove(Object... entities);

    <T> T remove(Class<T> entityClass, Object ID);

    <T> List<T> remove(Class<T> entityClass, Object... ID);

    int removeAll(Class<?> entityClass);

    // query by jpql

    <T> T findOne(Class<T> entityClass, Object ID);

    <T> T findOne(String jpql);

    <T> T findOne(String jpql, Object... parameters);

    <T> T findOne(String jpql, Map<String, Object> parameters);

    <T> List<T> findAll(Class<T> entityClass);

    <T> List<T> findAll(Class<T> entityClass, Sort sort);

    <T> List<T> findAll(String jpql);

    <T> List<T> findAll(String jpql, Object... parameters);

    <T> List<T> findAll(String jpql, Map<String, Object> parameters);

    long countAll(Class<?> entityClass);

    long count(String jpql);

    long count(String jpql, Object... parameters);

    long count(String jpql, Map<String, Object> parameters);

    int executeUpdate(String jpql, Object... parameters);

    int executeUpdate(String jpql, Map<String, Object> conditions);

    // query sql

    <T> T findOneBySQL(Class<T> requireType, String sql);

    <T> T findOneBySQL(Class<T> requireType, String sql, Object... parameters);

    <T> T findOneBySQL(Class<T> requireType, String sql, Map<String, Object> parameters);

    <T> List<T> findAllBySQL(Class<T> requireType, String sql);

    <T> List<T> findAllBySQL(Class<T> requireType, String sql, Object... parameters);

    <T> List<T> findAllBySQL(Class<T> requireType, String sql, Map<String, Object> parameters);

    long countBySQL(String sql);

    long countBySQL(String sql, Object... parameters);

    long countBySQL(String sql, Map<String, Object> parameters);

    int executeUpdateBySQL(String sql, Object... values);

    int executeUpdateBySQL(String sql, Map<String, Object> parameters);
}

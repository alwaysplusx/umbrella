/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.data.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.data.domain.Sort;

/**
 * 持久化与基础查询类
 * 
 * @author wuxii
 *
 */
public interface Dao {

    /**
     * 保存entity
     * 
     * @param entity
     *            待保存的entity
     * @return 保存后的entity
     * @throws IllegalArgumentException
     *             if not a new entity
     */
    <T> T save(T entity);

    /**
     * 批量保存entity
     * 
     * @param entities
     *            待保存的entities
     * @return 保存后的entities
     * @throws IllegalArgumentException
     *             if contains not new entity
     */
    <T> Iterable<T> save(Iterable<T> entities);

    /**
     * 更新entity方法
     * 
     * @param entity
     *            待更新的entity
     * @return 更新后的entity
     * @throws IllegalArgumentException
     *             if a new entity
     */
    <T> T update(T entity);

    /**
     * 批量更新entities
     * 
     * @param entities
     *            待更新的entities
     * @return 更新后的entities
     * @throws IllegalArgumentException
     *             if contains new entity
     */
    <T> Iterable<T> update(Iterable<T> entities);

    /**
     * 检查entity是否是为保存过的entity. 如检验id是否有值
     * 
     * @return {@code ture} is new one
     * @throws IllegalArgumentException
     *             if input not entity object
     */
    boolean isNew(Object entity);

    /**
     * 保存或更新entity
     * 
     * @param entity
     *            待处理的entity
     * @return 处理后的entity
     */
    <T> T saveOrUpdate(T entity);

    /**
     * 批量保存或更新entities
     * 
     * @param entities
     *            待处理的entities
     * @return 处理后的entities
     */
    <T> Iterable<T> saveOrUpdate(Iterable<T> entities);

    /**
     * 删除entity
     * 
     * @param entity
     *            待删除的entity
     * @throws IllegalArgumentException
     *             if not entity instance
     */
    void delete(Object entity);

    /**
     * 批量删除entities
     * 
     * @param entities
     *            待删除的entities
     * @throws IllegalArgumentException
     *             if contains not entity instance
     */
    <T> void delete(Iterable<T> entities);

    /**
     * 删除entityClass的所有数据
     * 
     * @param entityClass
     *            entity的类名
     * @return 删除的记录条数
     * @throws IllegalArgumentException
     *             if not entity class
     */
    int deleteAll(Class<?> entityClass);

    /**
     * 指定id删除entity, if id is null ignore
     * 
     * @param entityClass
     *            待删除的entity类名
     * @param id
     *            entity的id
     * @return 被删除的entity
     * @throws IllegalArgumentException
     *             if not entity class
     */
    <T> T delete(Class<T> entityClass, Serializable id);

    /**
     * 批量删除指定id的entity
     * 
     * @param entityClass
     *            待删除的entity类名
     * @param ids
     *            指定的id组
     * @return 被删除的entity
     */
    <T> Iterable<T> delete(Class<T> entityClass, Iterable<? extends Serializable> ids);

    /**
     * 通过id查询对应的entity
     * 
     * @param entityClass
     *            待查询的entity类
     * @param id
     *            指定的id
     * @return if not find return null else return entity of the specific id
     */
    <T> T findOne(Class<T> entityClass, Serializable id);

    /**
     * 根据JPQL查询对应的entity, if not find return null
     * 
     * @param jpql
     *            jpql语句
     * @return 查询出的entity
     * @throws NonUniqueResultException
     *             if more than one result
     */
    <T> T findOne(String jpql);

    /**
     * 带参数查询对应的entity, if not find return null
     * 
     * @param jpql
     *            jpql语句
     * @param parameters
     *            查询条件
     * @return 结果entity
     * @throws NonUniqueResultException
     *             if more than one result
     */
    <T> T findOne(String jpql, Object... parameters);

    /**
     * 带参数查询对应的entity, if not find return null
     * 
     * @param jpql
     *            jpql语句
     * @param parameters
     *            查询条件
     * @return 结果entity
     * @throws NonUniqueResultException
     *             if more than one result
     */
    <T> T findOne(String jpql, Map<String, Object> parameters);

    /**
     * 通过sql查询entity
     * 
     * @param sql
     *            sql语句
     * @param resultClass
     *            结果类
     * @return 查询的结果
     * @throws NonUniqueResultException
     *             if more than one result
     */
    <T> T findOneBySQL(String sql, Class<T> resultClass);

    /**
     * 通过sql查询entity
     * 
     * @param sql
     *            sql语句
     * @param resultClass
     *            结果类
     * @param parameters
     *            查询参数
     * @return 查询结果
     * @throws NonUniqueResultException
     *             if more than one result
     */
    <T> T findOneBySQL(String sql, Class<T> resultClass, Object... parameters);

    /**
     * 通过sql查询entity
     * 
     * @param sql
     *            sql语句
     * @param resultClass
     *            结果类
     * @param parameters
     *            查询参数
     * @return 查询结果
     * @throws NonUniqueResultException
     *             if more than one result
     */
    <T> T findOneBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters);

    /**
     * 查询所有entityClass对应的entity
     * 
     * @param entityClass
     *            待查询的entity类
     * @return 所有entity, if not have entity data return empty List
     */
    <T> List<T> findAll(Class<T> entityClass);

    /**
     * 带排序条件的查询,查询所有entityClass对应的entity
     * 
     * @param entityClass
     *            待查询的entity类
     * @param sort
     *            排序条件
     * @return 所有entity, if not have entity data return empty List
     */
    <T> List<T> findAll(Class<T> entityClass, Sort sort);

    /**
     * 通过jpql查询符合条件的数据
     * 
     * @param jpql
     *            jpql语句
     * @return 符合条件的数据
     */
    <T> List<T> findAll(String jpql);

    /**
     * 带参数查询符合条件的数据
     * 
     * @param jpql
     *            jpql语句
     * @param parameters
     *            查询参数
     * @return 符合条件的数据
     */
    <T> List<T> findAll(String jpql, Object... parameters);

    /**
     * 待参数查询符合条件的数据
     * 
     * @param jpql
     *            jpql语句
     * @param parameters
     *            查询参数
     * @return 符合条件的数据
     */
    <T> List<T> findAll(String jpql, Map<String, Object> parameters);

    /**
     * 使用sql查询符合条件的数据
     * 
     * @param sql
     *            sql语句
     * @param resultClass
     *            查询的结果类
     * @return 符合条件的数据
     */
    <T> List<T> findAllBySQL(String sql, Class<T> resultClass);

    /**
     * 带参数sql查询符合条件的数据
     * 
     * @param sql
     *            sql语句
     * @param resultClass
     *            查询的结果类
     * @param parameters
     *            查询参数
     * @return 符合条件的数据
     */
    <T> List<T> findAllBySQL(String sql, Class<T> resultClass, Object... parameters);

    /**
     * 带参数sql查询符合条件的数据
     * 
     * @param sql
     *            sql语句
     * @param resultClass
     *            查询的结果类
     * @param parameters
     *            查询参数
     * @return 符合条件的数据
     */
    <T> List<T> findAllBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters);

    /**
     * 统计entity的数据条数
     * 
     * @param entityClass
     *            entity的类
     * @return entity数据条数
     */
    long countAll(Class<?> entityClass);

    /**
     * 使用jpql统计数据条数
     * 
     * @param jpql
     *            统计jpql
     * @return 数据条数
     */
    long count(String jpql);

    /**
     * 使用jpql统计数据条数
     * 
     * @param jpql
     *            统计jpql
     * @param parameters
     *            查询参数
     * @return 数据条数
     */
    long count(String jpql, Object... parameters);

    /**
     * 使用jpql统计数据条数
     * 
     * @param jpql
     *            统计jpql
     * @param parameters
     *            查询参数
     * @return 数据条数
     */
    long count(String jpql, Map<String, Object> parameters);

    /**
     * 使用sql统计数据条数
     * 
     * @param sql
     *            统计sql
     * @return 数据条数
     */
    long countBySQL(String sql);

    /**
     * 使用sql统计数据条数
     * 
     * @param sql
     *            统计sql
     * @param parameters
     *            查询参数
     * @return 数据条数
     */
    long countBySQL(String sql, Object... parameters);

    /**
     * 使用sql统计数据条数
     * 
     * @param sql
     *            统计sql
     * @param parameters
     *            查询参数
     * @return 数据条数
     */
    long countBySQL(String sql, Map<String, Object> parameters);

    /**
     * 执行jpql语句
     * 
     * @param jpql
     *            jpql语句
     * @return 受影响的数据条数
     */
    int executeUpdate(String jpql);

    /**
     * 执行sql语句
     * 
     * @param sql
     *            sql语句
     * @return 守影响的数据条数
     */
    int executeUpdateBySQL(String sql);

}

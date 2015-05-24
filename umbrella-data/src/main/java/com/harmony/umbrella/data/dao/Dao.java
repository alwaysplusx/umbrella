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

	<T> T save(T entity);

	<T> Iterable<T> save(Iterable<T> entities);

	<T> T update(T entity);

	<T> Iterable<T> update(Iterable<T> entities);

	<T> T saveOrUpdate(T entity);

	<T> Iterable<T> saveOrUpdate(Iterable<T> entities);

	void delete(Object entity);

	<T> void delete(Iterable<T> entities);

	int deleteAll(Class<?> entityClass);

	<T> T delete(Class<T> entityClass, Serializable id);

	<T> Iterable<T> delete(Class<T> entityClass, Iterable<? extends Serializable> ids);

	<T> T findOne(Class<T> entityClass, Serializable id);

	<T> T findOne(String jpql);

	<T> T findOne(String jpql, Object... parameters);

	<T> T findOne(String jpql, Map<String, Object> parameters);

	<T> T findOneBySQL(String sql, Class<T> resultClass);

	<T> T findOneBySQL(String sql, Class<T> resultClass, Object... parameters);

	<T> T findOneBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters);

	<T> List<T> findAll(Class<T> entityClass);

	<T> List<T> findAll(Class<T> entityClass, Sort sort);

	<T> List<T> findAll(String jpql);

	<T> List<T> findAll(String jpql, Object... parameters);

	<T> List<T> findAll(String jpql, Map<String, Object> parameters);

	<T> List<T> findAllBySQL(String sql, Class<T> resultClass);

	<T> List<T> findAllBySQL(String sql, Class<T> resultClass, Object... parameters);

	<T> List<T> findAllBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters);

	long countAll(Class<?> entityClass);

	long count(String jpql);

	long count(String jpql, Object... parameters);

	long count(String jpql, Map<String, Object> parameters);

	long countBySQL(String sql);

	long countBySQL(String sql, Object... parameters);

	long countBySQL(String sql, Map<String, Object> parameters);

	int executeUpdate(String jpql);

	int executeUpdateBySQL(String sql);

}

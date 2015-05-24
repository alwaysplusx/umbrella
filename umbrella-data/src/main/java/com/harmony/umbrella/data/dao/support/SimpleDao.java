/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.data.dao.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.util.Assert;

import com.harmony.umbrella.data.dao.Dao;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.query.EntityInformation;
import com.harmony.umbrella.data.query.JpaEntityInformation;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleDao implements Dao {

	private static final String ENTITY_CLASS_MUST_NOT_BE_NULL = "The given entity class must not be null!";

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private final EntityManager em;

	@SuppressWarnings("unused")
	private final PersistenceProvider provider;

	public SimpleDao(EntityManager entityManager) {
		this.em = entityManager;
		this.provider = PersistenceProvider.fromEntityManager(entityManager);
	}

	@Override
	public <T> T save(T entity) {

		EntityInformation<T, Serializable> entityInfo = getEntityInformation(entity);

		if (entityInfo.isNew(entity)) {
			em.persist(entity);
			return entity;
		}

		throw new IllegalStateException("save failed! " + entity + " is not new entity");
	}

	@Override
	public <T> Iterable<T> save(Iterable<T> entities) {

		List<T> result = new ArrayList<T>();

		if (entities == null) {
			return result;
		}

		for (T entity : entities) {
			result.add(save(entity));
		}

		return result;
	}

	@Override
	public <T> T update(T entity) {

		EntityInformation<T, Serializable> entityInfo = getEntityInformation(entity);

		if (!entityInfo.isNew(entity)) {
			return em.merge(entity);
		}

		throw new IllegalStateException("update failed! " + entity + " is not exists");
	}

	@Override
	public <T> Iterable<T> update(Iterable<T> entities) {

		List<T> result = new ArrayList<T>();

		if (entities == null) {
			return result;
		}

		for (T entity : entities) {
			result.add(update(entity));
		}

		return result;
	}

	@Override
	public <T> T saveOrUpdate(T entity) {

		EntityInformation<Object, Serializable> entityInfo = getEntityInformation(entity);

		if (entityInfo.isNew(entity)) {
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}

	}

	@Override
	public <T> Iterable<T> saveOrUpdate(Iterable<T> entities) {
		List<T> result = new ArrayList<T>();

		if (entities == null) {
			return result;
		}

		for (T entity : entities) {
			result.add(saveOrUpdate(entity));
		}

		return result;
	}

	@Override
	public void delete(Object entity) {
		if (entity == null)
			return;
		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}

	@Override
	public <T> T delete(Class<T> entityClass, Serializable id) {
		T entity = findOne(entityClass, id);
		if (entity != null) {
			delete(entity);
		}
		return entity;
	}

	@Override
	public <T> Iterable<T> delete(Class<T> entityClass, Iterable<? extends Serializable> ids) {

		List<T> result = new ArrayList<T>();

		if (ids == null) {
			return result;
		}

		for (Serializable id : ids) {
			result.add(delete(entityClass, id));
		}

		return result;
	}

	@Override
	public <T> T findOne(Class<T> entityClass, Serializable id) {
		Assert.notNull(entityClass, ENTITY_CLASS_MUST_NOT_BE_NULL);

		if (id == null)
			return null;

		return em.find(entityClass, id);
	}

	@Override
	public <T> T findOne(String jpql) {
		return findOne(jpql, EMPTY_ARRAY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T findOne(String jpql, Object... parameters) {
		if (jpql == null)
			return null;
		Query query = em.createQuery(jpql);
		if (parameters != null) {
			for (int i = 0, max = parameters.length; i < max; i++) {
				query.setParameter(i + 1, parameters[i]);
			}
		}
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T findOne(String jpql, Map<String, Object> parameters) {
		if (jpql == null)
			return null;
		Query query = em.createQuery(jpql);

		if (parameters != null) {
			Iterator<String> keys = parameters.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				query.setParameter(key, parameters.get(key));
			}
		}
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public <T> T findOneBySQL(String sql, Class<T> resultClass) {
		return findOneBySQL(sql, resultClass, EMPTY_ARRAY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T findOneBySQL(String sql, Class<T> resultClass, Object... parameters) {
		if (sql == null)
			return null;

		Query query = null;

		if (resultClass == null) {
			query = em.createQuery(sql);
		} else {
			query = em.createQuery(sql, resultClass);
		}

		if (parameters != null) {
			for (int i = 0, max = parameters.length; i < max; i++) {
				query.setParameter(i + 1, parameters[i]);
			}
		}

		try {
			return (T) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public <T> T findOneBySQL(String sql, Map<String, Object> parameters) {
		return null;
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass) {
		return null;
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass, Sort sort) {
		return null;
	}

	@Override
	public <T> List<T> findAll(String jpql) {
		return null;
	}

	@Override
	public <T> List<T> findAll(String jpql, Object... parameters) {
		return null;
	}

	@Override
	public <T> List<T> findAll(String jpql, Map<String, Object> parameters) {
		return null;
	}

	@Override
	public <T> List<T> findAllBySQL(String sql, Class<T> resultClass) {
		return null;
	}

	@Override
	public <T> List<T> findAllBySQL(String sql, Class<T> resultClass, Object... parameters) {
		return null;
	}

	@Override
	public <T> List<T> findAllBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters) {
		return null;
	}

	@Override
	public long countAll(Class<?> entityClass) {
		return 0;
	}

	@Override
	public long count(String jpql) {
		return 0;
	}

	@Override
	public long count(String jpql, Object... parameters) {
		return 0;
	}

	@Override
	public long count(String jpql, Map<String, Object> parameters) {
		return 0;
	}

	@Override
	public long countBySQL(String sql) {
		return 0;
	}

	@Override
	public long countBySQL(String sql, Object... parameters) {
		return 0;
	}

	@Override
	public long countBySQL(String sql, Map<String, Object> parameters) {
		return 0;
	}

	@Override
	public int executeUpdate(String jpql) {
		return 0;
	}

	@Override
	public int executeUpdateBySQL(String sql) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	protected <T> Class<T> getDomainClass(Object entity) {
		return (Class<T>) entity.getClass();
	}

	@SuppressWarnings("unchecked")
	protected <T> EntityInformation<T, Serializable> getEntityInformation(Object entity) {
		return new JpaEntityInformation<T, Serializable>((Class<T>) entity.getClass(), em.getMetamodel());
	}

	protected <T> EntityInformation<T, Serializable> getEntityInformation(Class<T> entityClass) {
		return new JpaEntityInformation<T, Serializable>(entityClass, em.getMetamodel());
	}

}

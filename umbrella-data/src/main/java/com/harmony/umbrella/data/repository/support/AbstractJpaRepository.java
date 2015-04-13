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
package com.harmony.umbrella.data.repository.support;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.domain.Specification;
import com.harmony.umbrella.data.jpa.provider.PersistenceProvider;
import com.harmony.umbrella.data.query.EntityInformation;
import com.harmony.umbrella.data.repository.JpaRepository;
import com.harmony.umbrella.data.repository.JpaSpecificationExecutor;
import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractJpaRepository<T, ID extends Serializable> implements JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";

	// protected String getCountQueryString() {
	// EntityInformation<T, ?> entityInformation = getEntityInformation();
	// PersistenceProvider provider = getPersistenceProvider();
	// String countQuery = String.format(COUNT_QUERY_STRING,
	// provider.getCountQueryPlaceholder(), "%s");
	// return getQueryString(countQuery, entityInformation.getEntityName());
	// }

	@SuppressWarnings("unchecked")
	protected Class<T> getDomainClass() {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] types = pt.getActualTypeArguments();
			for (Type t : types) {
				if (t instanceof Class) {
					return (Class<T>) t;
				}
			}
		}
		throw new IllegalArgumentException("illegal generic in super class");
	}

	protected EntityManager getEntityManager() {
		return null;
	}

	protected EntityInformation<T, ?> getEntityInformation() {
		return null;
	}

	protected PersistenceProvider getPersistenceProvider() {
		return null;
	}

	@Override
	public <S extends T> S save(S entity) {
		if (getEntityInformation().isNew(entity)) {
			getEntityManager().persist(entity);
			return entity;
		} else {
			return getEntityManager().merge(entity);
		}
	}

	@Override
	public <S extends T> List<S> save(Iterable<S> entities) {
		List<S> result = new ArrayList<S>();
		if (entities == null) {
			return result;
		}
		for (S entity : entities) {
			result.add(save(entity));
		}
		return result;
	}

	@Override
	public <S extends T> S saveAndFlush(S entity) {
		S result = save(entity);
		flush();
		return result;
	}

	@Override
	public void flush() {
		getEntityManager().flush();
	}

	@Override
	public boolean exists(ID id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return false;
	}

	@Override
	public T delete(ID id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		T entity = findOne(id);
		if (entity != null) {
			delete(entity);
		}
		return entity;
	}

	@Override
	public void delete(T entity) {
		Assert.notNull(entity, "The entity must not be null!");
		EntityManager em = getEntityManager();
		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		for (T entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		for (T element : findAll()) {
			delete(element);
		}
	}

	@Override
	public void deleteInBatch(Iterable<T> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		if (!entities.iterator().hasNext()) {
			return;
		}
		// applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING,
		// entityInformation.getEntityName()), entities, em).executeUpdate();
	}

	@Override
	public void deleteAllInBatch() {
		// getEntityManager().createQuery(getDeleteAllQueryString()).executeUpdate();
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public long count(Specification<T> spec) {
		return 0;
	}

	@Override
	public T findOne(ID id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return getEntityManager().find(getDomainClass(), id);
	}

	@Override
	public T findOne(Specification<T> spec) {
		return null;
	}

	@Override
	public T getOne(ID id) {
		return null;
	}

	@Override
	public Page<T> findAll(Page<T> pageable) {
		return null;
	}

	@Override
	public List<T> findAll(Specification<T> spec) {
		return null;
	}

	@Override
	public Page<T> findAll(Specification<T> spec, Page<T> pageable) {
		return null;
	}

	@Override
	public List<T> findAll(Specification<T> spec, Sort sort) {
		return null;
	}

	@Override
	public List<T> findAll() {
		return null;
	}

	@Override
	public List<T> findAll(Sort sort) {
		return null;
	}

	@Override
	public List<T> findAll(Iterable<ID> ids) {
		return null;
	}

}

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.util.Assert;

import com.harmony.umbrella.data.domain.Specification;
import com.harmony.umbrella.data.query.EntityInformation;
import com.harmony.umbrella.data.query.JpaEntityInformation;
import com.harmony.umbrella.data.repository.BaseRepository;

/**
 * @author wuxii@foxmail.com
 */
public class GenericBaseRepository<ID extends Serializable> implements BaseRepository<ID> {

	private EntityManager entityManager;

	@Override
	public <S> S save(S entity) {
		entityManager.persist(entity);
		return entity;
	}

	@Override
	public <S> Iterable<S> save(Iterable<S> entities) {
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
	public void delete(Iterable<?> entities) {
		if (entities == null)
			return;
		for (Object entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void delete(Object entity) {
		if (entity == null)
			return;
		entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
	}

	@Override
	public <S> S delete(Class<S> domainClass, ID id) {
		S entity = findOne(domainClass, id);
		delete(entity);
		return entity;
	}

	@Override
	public void deleteAll(Class<?> domainClass) {
		for (Object element : findAll(domainClass)) {
			delete(element);
		}
	}

	@Override
	public <S> List<S> findAll(Class<S> domainClass) {
		return getQuery(domainClass, null).getResultList();
	}

	@Override
	public <S> Iterable<S> findAll(Class<S> domainClass, Iterable<ID> ids) {
		if (ids == null || !ids.iterator().hasNext()) {
			return Collections.emptyList();
		}
		ByIdsSpecification<S> specification = new ByIdsSpecification<S>(new JpaEntityInformation<S, Serializable>(domainClass, entityManager.getMetamodel()));
		TypedQuery<S> query = getQuery(domainClass, specification);
		return query.setParameter(specification.parameter, ids).getResultList();
	}

	@Override
	public <S> List<S> findAll(Class<S> domainClass, Specification<S> spec) {
		return getQuery(domainClass, spec).getResultList();
	}

	@Override
	public <S> S findOne(Class<S> domainClass, ID id) {
		Assert.notNull(domainClass, "entity class can't be null");
		Assert.notNull(id, "primaryKey can't be null");
		return entityManager.find(domainClass, id);
	}

	@Override
	public <S> S findOne(Class<S> domainClass, Specification<S> spec) {
		return getQuery(domainClass, spec).getSingleResult();
	}

	@Override
	public boolean exists(Class<?> domainClass, ID id) {
		return findOne(domainClass, id) != null;
	}

	@Override
	public <S> boolean exists(Class<S> domainClass, Specification<S> spec) {
		return count(domainClass, spec) > 0;
	}

	@Override
	public long count(Class<?> domainClass) {
		return getCountQuery(domainClass, null).getSingleResult();
	}

	@Override
	public <S> long count(Class<S> domainClass, Specification<S> spec) {
		return executeCountQuery(getCountQuery(domainClass, spec));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <S> TypedQuery<Long> getCountQuery(Class<?> domainClass, Specification<S> spec) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root root = query.from(domainClass);
		if (spec != null) {
			Predicate predicate = spec.toPredicate(root, query, builder);
			if (predicate != null) {
				query.where(predicate);
			}
		}
		if (query.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}
		return entityManager.createQuery(query);
	}

	protected <S> TypedQuery<S> getQuery(Class<S> domainClass, Specification<S> spec) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<S> query = builder.createQuery(domainClass);
		Root<S> root = query.from(domainClass);
		query.select(root);
		if (spec != null) {
			Predicate predicate = spec.toPredicate(root, query, builder);
			if (predicate != null) {
				query.where(predicate);
			}
		}
		return entityManager.createQuery(query);
	}

	private static Long executeCountQuery(TypedQuery<Long> query) {
		Assert.notNull(query);
		List<Long> totals = query.getResultList();
		Long total = 0L;
		for (Long element : totals) {
			total += element == null ? 0 : element;
		}
		return total;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Specification that gives access to the {@link Parameter} instance used to
	 * bind the ids for {@link SimpleJpaRepository#findAll(Iterable)}.
	 * Workaround for OpenJPA not binding collections to in-clauses correctly
	 * when using by-name binding.
	 * 
	 * @see https
	 *      ://issues.apache.org/jira/browse/OPENJPA-2018?focusedCommentId=
	 *      13924055
	 * @author Oliver Gierke
	 */
	@SuppressWarnings("rawtypes")
	static final class ByIdsSpecification<T> implements Specification<T> {

		private final EntityInformation<T, ?> entityInformation;

		ParameterExpression<Iterable> parameter;

		public ByIdsSpecification(EntityInformation<T, ?> entityInformation) {
			this.entityInformation = entityInformation;
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.data.jpa.domain.Specification#toPredicate(javax.persistence.criteria.Root, javax.persistence.criteria.CriteriaQuery, javax.persistence.criteria.CriteriaBuilder)
		 */
		public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

			Path<?> path = root.get(entityInformation.getIdAttribute());
			parameter = cb.parameter(Iterable.class);
			return path.in(parameter);
		}
	}
}

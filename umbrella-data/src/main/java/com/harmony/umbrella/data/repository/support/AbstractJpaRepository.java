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

import static com.harmony.umbrella.data.query.QueryUtils.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Parameter;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageImpl;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.domain.Specification;
import com.harmony.umbrella.data.jpa.provider.PersistenceProvider;
import com.harmony.umbrella.data.query.EntityInformation;
import com.harmony.umbrella.data.query.JpaEntityInformation;
import com.harmony.umbrella.data.repository.JpaRepository;
import com.harmony.umbrella.data.repository.JpaSpecificationExecutor;
import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJpaRepository<T, ID extends Serializable> implements JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";

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

	protected abstract EntityManager getEntityManager();

	protected EntityInformation<T, ?> getEntityInformation() {
		return new JpaEntityInformation<T, ID>(getDomainClass(), getEntityManager().getMetamodel());
	}

	protected PersistenceProvider getPersistenceProvider() {
		return PersistenceProvider.fromEntityManager(getEntityManager());
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
		if (getEntityInformation().getIdAttribute() != null) {
			return findOne(id) != null;
		}
		EntityInformation<T, ?> entityInfo = getEntityInformation();
		String entityName = entityInfo.getEntityName();
		Iterable<String> idAttributeNames = entityInfo.getIdAttributeNames();
		String placeholder = getPersistenceProvider().getCountQueryPlaceholder();
		String existsQuery = getExistsQueryString(entityName, placeholder, idAttributeNames);

		TypedQuery<Long> query = getEntityManager().createQuery(existsQuery, Long.class);

		if (!entityInfo.hasCompositeId()) {
			query.setParameter(idAttributeNames.iterator().next(), id);
			return query.getSingleResult() == 1L;
		}

		for (String idAttributeName : idAttributeNames) {
			Object idAttributeValue = entityInfo.getCompositeIdAttributeValue(id, idAttributeName);
			boolean complexIdParameterValueDiscovered = idAttributeValue != null && !query.getParameter(idAttributeName).getParameterType().isAssignableFrom(idAttributeValue.getClass());
			if (complexIdParameterValueDiscovered) {
				return findOne(id) != null;
			}
			query.setParameter(idAttributeName, idAttributeValue);
		}
		return query.getSingleResult() == 1L;
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
		EntityInformation<T, ?> entityInfo = getEntityInformation();
		EntityManager em = getEntityManager();
		applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING, entityInfo.getEntityName()), entities, em).executeUpdate();
	}

	@Override
	public void deleteAllInBatch() {
		getEntityManager().createQuery(getDeleteAllQueryString()).executeUpdate();
	}

	@Override
	public long count() {
		return getEntityManager().createQuery(getCountQueryString(), Long.class).getSingleResult();
	}

	@Override
	public long count(Specification<T> spec) {
		return executeCountQuery(getCountQuery(spec));
	}

	@Override
	public T findOne(ID id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return getEntityManager().find(getDomainClass(), id);
	}

	@Override
	public T findOne(Specification<T> spec) {
		try {
			return getQuery(spec, (Sort) null).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public T getOne(ID id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return getEntityManager().getReference(getDomainClass(), id);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		if (null == pageable) {
			return new PageImpl<T>(findAll());
		}
		return findAll(null, pageable);
	}

	@Override
	public List<T> findAll(Specification<T> spec) {
		return getQuery(spec, (Sort) null).getResultList();
	}

	@Override
	public Page<T> findAll(Specification<T> spec, Pageable pageable) {
		TypedQuery<T> query = getQuery(spec, pageable);
		return pageable == null ? new PageImpl<T>(query.getResultList()) : readPage(query, pageable, spec);
	}

	@Override
	public List<T> findAll(Specification<T> spec, Sort sort) {
		return getQuery(spec, sort).getResultList();
	}

	@Override
	public List<T> findAll() {
		return getQuery(null, (Sort) null).getResultList();
	}

	@Override
	public List<T> findAll(Sort sort) {
		return getQuery(null, sort).getResultList();
	}

	@Override
	public List<T> findAll(Iterable<ID> ids) {
		if (ids == null || !ids.iterator().hasNext()) {
			return Collections.emptyList();
		}
		EntityInformation<T, ?> entityInfo = getEntityInformation();
		if (entityInfo.hasCompositeId()) {
			List<T> results = new ArrayList<T>();
			for (ID id : ids) {
				results.add(findOne(id));
			}
			return results;
		}

		ByIdsSpecification<T> specification = new ByIdsSpecification<T>(entityInfo);
		TypedQuery<T> query = getQuery(specification, (Sort) null);
		return query.setParameter(specification.parameter, ids).getResultList();

	}

	/**
	 * Creates a new count query for the given {@link Specification}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @return
	 */
	protected TypedQuery<Long> getCountQuery(Specification<T> spec) {
		EntityManager em = getEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = applySpecificationToCriteria(spec, query);
		if (query.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}
		return em.createQuery(query);
	}

	/**
	 * Reads the given {@link TypedQuery} into a {@link Page} applying the given
	 * {@link Pageable} and {@link Specification}.
	 * 
	 * @param query
	 *            must not be {@literal null}.
	 * @param spec
	 *            can be {@literal null}.
	 * @param pageable
	 *            can be {@literal null}.
	 * @return
	 */
	protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, Specification<T> spec) {

		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Long total = executeCountQuery(getCountQuery(spec));
		List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T> emptyList();

		return new PageImpl<T>(content, pageable, total);
	}

	/**
	 * Creates a new {@link TypedQuery} from the given {@link Specification}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @param pageable
	 *            can be {@literal null}.
	 * @return
	 */
	protected TypedQuery<T> getQuery(Specification<T> spec, Pageable pageable) {
		Sort sort = pageable == null ? null : pageable.getSort();
		return getQuery(spec, sort);
	}

	/**
	 * Creates a {@link TypedQuery} for the given {@link Specification} and
	 * {@link Sort}.
	 *
	 * @param spec
	 *            can be {@literal null}.
	 * @param sort
	 *            can be {@literal null}.
	 * @return
	 */
	protected TypedQuery<T> getQuery(Specification<T> spec, Sort sort) {
		EntityManager em = getEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(getDomainClass());

		Root<T> root = applySpecificationToCriteria(spec, query);
		query.select(root);

		if (sort != null) {
			query.orderBy(toOrders(sort, root, builder));
		}

		return applyRepositoryMethodMetadata(em.createQuery(query));
	}

	private TypedQuery<T> applyRepositoryMethodMetadata(TypedQuery<T> query) {
		return query;
	}

	//
	// /**
	// * Returns a {@link Map} with the query hints based on the current
	// * {@link CrudMethodMetadata} and potential {@link EntityGraph}
	// information.
	// *
	// * @return
	// */
	// protected Map<String, Object> getQueryHints() {
	//
	// if (metadata.getEntityGraph() == null) {
	// return metadata.getQueryHints();
	// }
	//
	// Map<String, Object> hints = new HashMap<String, Object>();
	// hints.putAll(metadata.getQueryHints());
	// hints.putAll(Jpa21Utils.tryGetFetchGraphHints(em,
	// metadata.getEntityGraph()));
	//
	// return hints;
	// }
	//
	// private void applyQueryHints(Query query) {
	// for (Entry<String, Object> hint : getQueryHints().entrySet()) {
	// query.setHint(hint.getKey(), hint.getValue());
	// }
	// }

	/**
	 * Applies the given {@link Specification} to the given
	 * {@link CriteriaQuery}.
	 * 
	 * @param spec
	 *            can be {@literal null}.
	 * @param query
	 *            must not be {@literal null}.
	 * @return
	 */
	private <S> Root<T> applySpecificationToCriteria(Specification<T> spec, CriteriaQuery<S> query) {
		Assert.notNull(query);
		Root<T> root = query.from(getDomainClass());
		if (spec == null) {
			return root;
		}
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		Predicate predicate = spec.toPredicate(root, query, builder);
		if (predicate != null) {
			query.where(predicate);
		}
		return root;
	}

	private String getDeleteAllQueryString() {
		return getQueryString(DELETE_ALL_QUERY_STRING, getEntityInformation().getEntityName());
	}

	private String getCountQueryString() {
		String countQuery = String.format(COUNT_QUERY_STRING, getPersistenceProvider().getCountQueryPlaceholder(), "%s");
		return getQueryString(countQuery, getEntityInformation().getEntityName());
	}

	/**
	 * Executes a count query and transparently sums up all values returned.
	 * 
	 * @param query
	 *            must not be {@literal null}.
	 * @return
	 */
	private static Long executeCountQuery(TypedQuery<Long> query) {
		Assert.notNull(query);
		List<Long> totals = query.getResultList();
		Long total = 0L;
		for (Long element : totals) {
			total += element == null ? 0 : element;
		}
		return total;
	}

	// /**
	// * Creates a criteria API {@link javax.persistence.criteria.Order} from
	// the
	// * given {@link Order}.
	// *
	// * @param order
	// * the order to transform into a JPA
	// * {@link javax.persistence.criteria.Order}
	// * @param root
	// * the {@link Root} the {@link Order} expression is based on
	// * @param cb
	// * the {@link CriteriaBuilder} to build the
	// * {@link javax.persistence.criteria.Order} with
	// * @return
	// */
	// @SuppressWarnings("unchecked")
	// private static javax.persistence.criteria.Order toJpaOrder(Order order,
	// Root<?> root, CriteriaBuilder cb) {
	//
	// PropertyPath property = PropertyPath.from(order.getProperty(),
	// root.getJavaType());
	// Expression<?> expression = toExpressionRecursively(root, property);
	//
	// if (order.isIgnoreCase() &&
	// String.class.equals(expression.getJavaType())) {
	// Expression<String> lower = cb.lower((Expression<String>) expression);
	// return order.isAscending() ? cb.asc(lower) : cb.desc(lower);
	// } else {
	// return order.isAscending() ? cb.asc(expression) : cb.desc(expression);
	// }
	// }

	/**
	 * Turns the given {@link Sort} into
	 * {@link javax.persistence.criteria.Order}s.
	 * 
	 * @param sort
	 *            the {@link Sort} instance to be transformed into JPA
	 *            {@link javax.persistence.criteria.Order}s.
	 * @param root
	 *            must not be {@literal null}.
	 * @param cb
	 *            must not be {@literal null}.
	 * @return
	 */
	public static List<javax.persistence.criteria.Order> toOrders(Sort sort, Root<?> root, CriteriaBuilder cb) {

		List<javax.persistence.criteria.Order> orders = new ArrayList<javax.persistence.criteria.Order>();

		// if (sort == null) {
		// return orders;
		// }
		//
		// Assert.notNull(root);
		// Assert.notNull(cb);
		//
		// for (Sort.Order order : sort) {
		// orders.add(toJpaOrder(order, root, cb));
		// }

		return orders;
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
	private static final class ByIdsSpecification<T> implements Specification<T> {

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

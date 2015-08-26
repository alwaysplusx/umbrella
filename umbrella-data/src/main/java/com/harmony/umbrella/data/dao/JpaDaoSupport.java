/*
 * Copyright 2002-2014 the original author or authors.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.EntityInformation;
import com.harmony.umbrella.data.JpaDao;
import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageImpl;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.query.JpaEntityInformation;
import com.harmony.umbrella.data.query.QueryUtils;
import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public abstract class JpaDaoSupport<E, ID extends Serializable> extends DaoSupport implements JpaDao<E, ID> {

    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x";

    private EntityInformation<E, ID> ei;

    protected abstract Class<E> getEntityClass();

    protected EntityInformation<E, ID> getEntityInformation() {
        if (ei == null) {
            ei = new JpaEntityInformation<E, ID>(getEntityClass(), getEntityManager().getMetamodel());
        }
        return ei;
    }

    @Override
    public Page<E> findAll(Pageable pageable) {

        if (null == pageable) {
            return new PageImpl<E>(findAll());
        }

        return findAll(null, pageable);
    }

    @Override
    public Iterable<E> findAll(Sort sort) {
        return getQuery(null, sort).getResultList();
    }

    @Override
    public long count() {
        return countAll(getEntityInformation().getJavaType());
    }

    @Override
    public void deleteAll() {
        deleteAll(getEntityInformation().getJavaType());
    }

    @Override
    public E findOne(ID id) {
        return findOne(getEntityInformation().getJavaType(), id);
    }

    @Override
    public List<E> findAll() {
        return findAll(getEntityInformation().getJavaType());
    }

    @Override
    public List<E> findAll(Iterable<ID> ids) {
        if (ids == null || !ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        if (getEntityInformation().hasCompositeId()) {
            List<E> results = new ArrayList<E>();
            for (ID id : ids) {
                results.add(findOne(id));
            }
            return results;
        }

        ByIdsSpecification<E> specification = new ByIdsSpecification<E>(getEntityInformation());
        TypedQuery<E> query = getQuery(specification, (Sort) null);

        return query.setParameter(specification.parameter, ids).getResultList();
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public E saveAndFlush(E entity) {

        E result = save(entity);
        flush();

        return result;
    }

    @Override
    public void deleteInBatch(Iterable<E> entities) {
        if (entities == null) {
            return;
        }
        if (!entities.iterator().hasNext()) {
            return;
        }

        StringBuilder buffer = new StringBuilder(String.format(DELETE_ALL_QUERY_STRING, getEntityInformation().getEntityName()));

        buffer.append(" where");

        Iterator<E> iterator = entities.iterator();

        int i = 0;

        while (iterator.hasNext()) {

            iterator.next();

            buffer.append(String.format(" %s = ?%d", "x", ++i));

            if (iterator.hasNext()) {
                buffer.append(" or");
            }
        }

        Query query = getEntityManager().createQuery(buffer.toString());

        i = 0;
        iterator = entities.iterator();

        while (iterator.hasNext()) {
            query.setParameter(++i, iterator.next());
        }

        query.executeUpdate();

    }

    @Override
    public void deleteAllInBatch() {
        deleteAll(getEntityInformation().getJavaType());
    }

    @Override
    public E getOne(ID id) {
        if (id == null)
            return null;
        return getEntityManager().getReference(getEntityInformation().getJavaType(), id);
    }

    @Override
    public E findOne(Specification<E> spec) {
        try {
            return getQuery(spec, null).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<E> findAll(Specification<E> spec) {
        return getQuery(spec, null).getResultList();
    }

    @Override
    public Page<E> findAll(Specification<E> spec, Pageable pageable) {

        TypedQuery<E> query = getQuery(spec, pageable != null ? pageable.getSort() : null);

        return pageable == null ? new PageImpl<E>(query.getResultList()) : readPage(query, pageable, spec);
    }

    @Override
    public List<E> findAll(Specification<E> spec, Sort sort) {
        return getQuery(spec, sort).getResultList();
    }

    @Override
    public long count(Specification<E> spec) {
        return executeCountQuery(getCountQuery(spec));
    }

    @Override
    public boolean exists(ID id) {
        if (id == null)
            return false;

        return findOne(id) != null;
    }

    @Override
    public E findOneBySQL(String sql) {
        return findOneBySQL(sql, getEntityInformation().getJavaType());
    }

    @Override
    public E findOneBySQL(String sql, Map<String, Object> parameters) {
        return findOneBySQL(sql, getEntityInformation().getJavaType(), parameters);
    }

    @Override
    public E findOneBySQL(String sql, Object... parameters) {
        return findOneBySQL(sql, getEntityInformation().getJavaType(), parameters);
    }

    @Override
    public List<E> findAllBySQL(String sql) {
        return findAllBySQL(sql, getEntityInformation().getJavaType());
    }

    @Override
    public List<E> findAllBySQL(String sql, Map<String, Object> parameters) {
        return findAllBySQL(sql, getEntityInformation().getJavaType(), parameters);
    }

    @Override
    public List<E> findAllBySQL(String sql, Object... parameters) {
        return findAllBySQL(sql, getEntityInformation().getJavaType(), parameters);
    }

    @Override
    public boolean exists(Specification<E> spec) {
        return executeCountQuery(getCountQuery(spec)) > 0;
    }

    protected TypedQuery<E> getQuery(Specification<E> spec, Sort sort) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(getEntityInformation().getJavaType());

        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sort != null) {
            query.orderBy(QueryUtils.toJpaOrders(sort, root, builder));
        }

        return getEntityManager().createQuery(query);
    }

    protected Page<E> readPage(TypedQuery<E> query, Pageable pageable, Specification<E> spec) {
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = executeCountQuery(getCountQuery(spec));
        List<E> content = total > pageable.getOffset() ? query.getResultList() : Collections.<E> emptyList();

        return new PageImpl<E>(content, pageable, total);
    }

    protected TypedQuery<Long> getCountQuery(Specification<E> spec) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<E> root = applySpecificationToCriteria(spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        return getEntityManager().createQuery(query);
    }

    protected EntityInformation<E, ID> getEntityInformation(Class<E> entityClass) {
        return new JpaEntityInformation<E, ID>(entityClass, getEntityManager().getMetamodel());
    }

    private <S> Root<E> applySpecificationToCriteria(Specification<E> spec, CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<E> root = query.from(getEntityInformation().getJavaType());

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        if (root.getFetches().size() > 0 && !query.isDistinct()) {
            query.distinct(true);
        }

        return root;
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

    private static final class ByIdsSpecification<T> implements Specification<T> {

        private final EntityInformation<T, ?> entityInformation;

        @SuppressWarnings("rawtypes")
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

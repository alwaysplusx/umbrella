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
package com.harmony.umbrella.data.dao.support;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.dao.Dao;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.jpa.provider.PersistenceProvider;
import com.harmony.umbrella.data.query.EntityInformation;
import com.harmony.umbrella.data.query.JpaEntityInformation;
import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public abstract class DaoSupport implements Dao {

    private static final String ENTITY_CLASS_MUST_NOT_BE_NULL = "The given entity class must not be null!";

    private static final Object[] EMPTY_ARRAY = new Object[0];

    protected abstract EntityManager getEntityManager();

    protected PersistenceProvider getPersistenceProvider() {
        return PersistenceProvider.fromEntityManager(getEntityManager());
    }

    @Override
    public <T> T save(T entity) {

        EntityInformation<Object, ? extends Serializable> entityInfo = getEntityInformation(entity);

        if (entityInfo.isNew(entity)) {
            getEntityManager().persist(entity);
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

        EntityInformation<Object, ? extends Serializable> entityInfo = getEntityInformation(entity);

        if (!entityInfo.isNew(entity)) {
            return getEntityManager().merge(entity);
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

        EntityInformation<Object, ? extends Serializable> entityInfo = getEntityInformation(entity);

        if (entityInfo.isNew(entity)) {
            getEntityManager().persist(entity);
            return entity;
        } else {
            return getEntityManager().merge(entity);
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

        getEntityManager().remove(getEntityManager().contains(entity) ? entity : getEntityManager().merge(entity));
    }

    @Override
    public <T> void delete(Iterable<T> entities) {

        if (entities != null) {

            for (T entity : entities) {
                delete(entity);
            }

        }
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int deleteAll(Class<?> entityClass) {

        if (entityClass == null)
            throw new IllegalArgumentException(ENTITY_CLASS_MUST_NOT_BE_NULL);

        CriteriaDelete cd = getEntityManager().getCriteriaBuilder().createCriteriaDelete(entityClass);

        cd.from(entityClass);

        return getEntityManager().createQuery(cd).executeUpdate();

    }

    @Override
    public <T> T findOne(Class<T> entityClass, Serializable id) {
        Assert.notNull(entityClass, ENTITY_CLASS_MUST_NOT_BE_NULL);

        if (id == null)
            return null;

        return getEntityManager().find(entityClass, id);
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

        try {
            return (T) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findOne(String jpql, Map<String, Object> parameters) {

        if (jpql == null)
            return null;

        try {
            return (T) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult();
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
            query = getEntityManager().createNativeQuery(sql);
        } else {
            query = getEntityManager().createNativeQuery(sql, resultClass);
        }

        try {
            return (T) applyParameterToQuery(query, parameters).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findOneBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters) {
        if (sql == null)
            return null;

        Query query = null;

        if (resultClass == null) {
            query = getEntityManager().createNativeQuery(sql);
        } else {
            query = getEntityManager().createNativeQuery(sql, resultClass);
        }

        try {
            return (T) applyParameterToQuery(query, parameters).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return findAll(entityClass, null);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, Sort sort) {
        if (entityClass == null) {
            return new ArrayList<T>();
        }

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        query.from(entityClass);

        if (sort != null) {
            query.orderBy(toOrders(sort, query.from(entityClass), builder));
        }

        return getEntityManager().createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAll(String jpql) {

        if (jpql == null)
            return new ArrayList<T>();

        return getEntityManager().createQuery(jpql).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAll(String jpql, Object... parameters) {
        if (jpql == null)
            return new ArrayList<T>();

        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAll(String jpql, Map<String, Object> parameters) {
        if (jpql == null)
            return new ArrayList<T>();

        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAllBySQL(String sql, Class<T> resultClass) {
        if (sql == null)
            return new ArrayList<T>();

        if (resultClass == null) {
            return getEntityManager().createNativeQuery(sql).getResultList();
        } else {
            return getEntityManager().createNativeQuery(sql, resultClass).getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAllBySQL(String sql, Class<T> resultClass, Object... parameters) {
        if (sql == null)
            return new ArrayList<T>();

        Query query = null;
        if (resultClass == null) {
            query = getEntityManager().createNativeQuery(sql);
        } else {
            query = getEntityManager().createNativeQuery(sql, resultClass);
        }

        return applyParameterToQuery(query, parameters).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAllBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters) {
        if (sql == null)
            return new ArrayList<T>();

        Query query = null;
        if (resultClass == null) {
            query = getEntityManager().createNativeQuery(sql);
        } else {
            query = getEntityManager().createNativeQuery(sql, resultClass);
        }

        return applyParameterToQuery(query, parameters).getResultList();
    }

    @Override
    public long countAll(Class<?> entityClass) {
        if (entityClass == null)
            throw new IllegalArgumentException(ENTITY_CLASS_MUST_NOT_BE_NULL);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        query.select(builder.function("count", Long.class, query.from(entityClass)));

        return getEntityManager().createQuery(query).getSingleResult();
    }

    @Override
    public long count(String jpql) {
        if (jpql == null)
            throw new IllegalArgumentException("count query must not be null");

        return ((Number) getEntityManager().createQuery(jpql).getSingleResult()).longValue();
    }

    @Override
    public long count(String jpql, Object... parameters) {
        if (jpql == null)
            throw new IllegalArgumentException("count query must not be null");

        return ((Number) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult()).longValue();
    }

    @Override
    public long count(String jpql, Map<String, Object> parameters) {
        if (jpql == null)
            throw new IllegalArgumentException("count query must not be null");

        return ((Number) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult()).longValue();
    }

    @Override
    public long countBySQL(String sql) {
        if (sql == null)
            throw new IllegalArgumentException("count query must not be null");

        return ((Number) getEntityManager().createNativeQuery(sql).getSingleResult()).longValue();
    }

    @Override
    public long countBySQL(String sql, Object... parameters) {
        if (sql == null)
            throw new IllegalArgumentException("count query must not be null");

        return ((Number) getEntityManager().createNativeQuery(sql).getSingleResult()).longValue();
    }

    @Override
    public long countBySQL(String sql, Map<String, Object> parameters) {
        if (sql == null)
            throw new IllegalArgumentException("count query must not be null");

        return ((Number) applyParameterToQuery(getEntityManager().createNativeQuery(sql), parameters).getSingleResult()).longValue();
    }

    @Override
    public int executeUpdate(String jpql) {
        if (jpql == null)
            throw new IllegalArgumentException("execute query must not be null");

        return getEntityManager().createQuery(jpql).executeUpdate();
    }

    @Override
    public int executeUpdateBySQL(String sql) {
        if (sql == null)
            throw new IllegalArgumentException("execute query must not be null");

        return getEntityManager().createNativeQuery(sql).executeUpdate();
    }

    protected List<Order> toOrders(Sort sort, Root<?> root, CriteriaBuilder builder) {
        // TODO TO orders
        return new ArrayList<Order>();
    }

    protected <T extends Query> T applyParameterToQuery(T query, Object[] parameters) {
        Assert.notNull(query, "query must not be null");
        if (parameters != null && parameters.length > 0) {
            for (int i = 0, max = parameters.length; i < max; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
        }
        return query;
    }

    protected <T extends Query> T applyParameterToQuery(T query, Map<String, Object> parameters) {
        Assert.notNull(query, "query must not be null");
        if (parameters != null) {
            Iterator<String> keys = parameters.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                query.setParameter(key, parameters.get(key));
            }
        }
        return query;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> Class<T> getDomainClass(Object obj) {
        if (obj instanceof Class) {
            return (Class) obj;
        }
        Class clazz = obj.getClass();
        if (Proxy.isProxyClass(clazz)) {
            return (Class) Proxy.getInvocationHandler(obj).getClass();
        }
        return (Class) obj.getClass();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> EntityInformation<T, ? extends Serializable> getEntityInformation(Object entity) {
        Class domainClass = getDomainClass(entity);
        return new JpaEntityInformation<T, Serializable>(domainClass, getEntityManager().getMetamodel());
    }
}

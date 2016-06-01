package com.harmony.umbrella.data.dao;

import java.io.Serializable;
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
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.Dao;
import com.harmony.umbrella.data.DefaultEntityMetadata;
import com.harmony.umbrella.data.EntityMetadata;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.jpa.provider.PersistenceProvider;
import com.harmony.umbrella.data.util.QueryUtils;
import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public abstract class DaoSupport implements Dao {

    private static final String ENTITY_CLASS_MUST_NOT_BE_NULL = "The given entity class must not be null!";

    private static final Object[] EMPTY_ARRAY = new Object[0];

    private PersistenceProvider provider;

    protected abstract EntityManager getEntityManager();

    protected PersistenceProvider getPersistenceProvider() {
        if (provider == null) {
            provider = PersistenceProvider.fromEntityManager(getEntityManager());
        }
        return provider;
    }

    @Override
    public <E> EntityMetadata<E, ? extends Serializable> getEntityMetadata(Class<E> entityClass) {
        return new DefaultEntityMetadata<E, Serializable>(entityClass, getEntityManager().getMetamodel());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean isNew(Object entity) {
        EntityMetadata<Object, ? extends Serializable> entityMetadata = getEntityMetadata((Class) entity.getClass());
        return entityMetadata.isNew(entity);
    }

    @Override
    public <T> T save(T entity) {
        if (isNew(entity)) {
            getEntityManager().persist(entity);
        } else {
            getEntityManager().merge(entity);
        }
        return entity;
    }

    @Override
    public <T> Iterable<T> save(Iterable<T> entities) {
        Assert.notNull(entities, "save entities is null");
        List<T> result = new ArrayList<T>();
        for (T entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public <T> T update(T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> entities) {
        Assert.notNull(entities, "update entities is null");
        List<T> result = new ArrayList<T>();
        for (T entity : entities) {
            result.add(update(entity));
        }
        return result;
    }

    @Override
    public void delete(Object entity) {
        if (entity == null) {
            return;
        }
        EntityManager em = getEntityManager();
        em.remove(em.contains(entity) ? entity : em.merge(entity));
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
        if (ids == null || !ids.iterator().hasNext()) {
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
        Assert.notNull(entityClass, ENTITY_CLASS_MUST_NOT_BE_NULL);
        CriteriaDelete cd = getEntityManager().getCriteriaBuilder().createCriteriaDelete(entityClass);
        cd.from(entityClass);
        return getEntityManager().createQuery(cd).executeUpdate();
    }

    @Override
    public <T> T findOne(Class<T> entityClass, Serializable id) {
        Assert.notNull(entityClass, ENTITY_CLASS_MUST_NOT_BE_NULL);
        Assert.notNull(id, "id is null");
        return getEntityManager().find(entityClass, id);
    }

    @Override
    public <T> T findOne(String jpql) {
        return findOne(jpql, EMPTY_ARRAY);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findOne(String jpql, Object... parameters) {
        Assert.notBlank(jpql, "query jpql is null");
        try {
            return (T) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findOne(String jpql, Map<String, Object> parameters) {
        Assert.notBlank(jpql, "query jpql is null");
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
        Assert.notBlank(sql, "query sql is null");
        EntityManager em = getEntityManager();
        Query query = (resultClass == null) ? em.createNativeQuery(sql) : em.createNativeQuery(sql, resultClass);
        try {
            return (T) applyParameterToQuery(query, parameters).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findOneBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters) {
        Assert.notBlank(sql, "query sql is null");
        EntityManager em = getEntityManager();
        Query query = (resultClass == null) ? em.createNativeQuery(sql) : em.createNativeQuery(sql, resultClass);
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
        Assert.notNull(entityClass, ENTITY_CLASS_MUST_NOT_BE_NULL);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        if (sort != null) {
            query.orderBy(QueryUtils.toJpaOrders(sort, root, builder));
        }

        return getEntityManager().createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAll(String jpql) {
        Assert.notBlank(jpql, "query jpql is null");
        return getEntityManager().createQuery(jpql).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAll(String jpql, Object... parameters) {
        Assert.notBlank(jpql, "query jpql is null");
        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAll(String jpql, Map<String, Object> parameters) {
        Assert.notBlank(jpql, "query jpql is null");
        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAllBySQL(String sql, Class<T> resultClass) {
        Assert.notBlank(sql, "query sql is null");
        EntityManager em = getEntityManager();
        Query query = (resultClass == null) ? em.createNativeQuery(sql) : em.createNativeQuery(sql, resultClass);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAllBySQL(String sql, Class<T> resultClass, Object... parameters) {
        Assert.notBlank(sql, "query sql is null");

        EntityManager em = getEntityManager();
        Query query = (resultClass == null) ? em.createNativeQuery(sql) : em.createNativeQuery(sql, resultClass);

        return applyParameterToQuery(query, parameters).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAllBySQL(String sql, Class<T> resultClass, Map<String, Object> parameters) {
        Assert.notBlank(sql, "query sql is null");
        EntityManager em = getEntityManager();
        Query query = (resultClass == null) ? em.createNativeQuery(sql) : em.createNativeQuery(sql, resultClass);
        return applyParameterToQuery(query, parameters).getResultList();
    }

    @Override
    public long countAll(Class<?> entityClass) {
        Assert.notNull(entityClass, ENTITY_CLASS_MUST_NOT_BE_NULL);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        query.select(builder.function("count", Long.class, query.from(entityClass)));

        return getEntityManager().createQuery(query).getSingleResult();
    }

    @Override
    public long count(String jpql) {
        Assert.notBlank(jpql, "query jpql is null");
        return ((Number) getEntityManager().createQuery(jpql).getSingleResult()).longValue();
    }

    @Override
    public long count(String jpql, Object... parameters) {
        Assert.notBlank(jpql, "query jpql is null");
        return ((Number) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult()).longValue();
    }

    @Override
    public long count(String jpql, Map<String, Object> parameters) {
        Assert.notBlank(jpql, "query jpql is null");
        return ((Number) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult()).longValue();
    }

    @Override
    public long countBySQL(String sql) {
        Assert.notBlank(sql, "query sql is null");
        return ((Number) getEntityManager().createNativeQuery(sql).getSingleResult()).longValue();
    }

    @Override
    public long countBySQL(String sql, Object... parameters) {
        Assert.notBlank(sql, "query sql is null");
        return ((Number) getEntityManager().createNativeQuery(sql).getSingleResult()).longValue();
    }

    @Override
    public long countBySQL(String sql, Map<String, Object> parameters) {
        Assert.notBlank(sql, "query sql is null");
        return ((Number) applyParameterToQuery(getEntityManager().createNativeQuery(sql), parameters).getSingleResult()).longValue();
    }

    @Override
    public int executeUpdate(String jpql) {
        Assert.notBlank(jpql, "query jpql is null");
        return getEntityManager().createQuery(jpql).executeUpdate();
    }

    @Override
    public int executeUpdate(String jpql, Map<String, Object> parameters) {
        Assert.notBlank(jpql, "query jpql is null");
        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).executeUpdate();
    }

    @Override
    public int executeUpdateBySQL(String sql) {
        Assert.notBlank(sql, "query sql is null");
        return getEntityManager().createNativeQuery(sql).executeUpdate();
    }

    @Override
    public int executeUpdateBySQL(String sql, Map<String, Object> parameters) {
        Assert.notBlank(sql, "query sql is null");
        return applyParameterToQuery(getEntityManager().createNativeQuery(sql), parameters).executeUpdate();
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
        if (parameters != null && !parameters.isEmpty()) {
            Iterator<String> keys = parameters.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    query.getParameter(key);
                } catch (IllegalArgumentException e) {
                    // ensure specified parameter name exist
                    continue;
                }
                query.setParameter(key, parameters.get(key));
            }
        }
        return query;
    }

}

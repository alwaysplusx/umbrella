package com.harmony.umbrella.data.dao;

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

import org.springframework.data.domain.Sort;

import com.harmony.umbrella.data.util.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class DAOSupport implements DAO {

    protected abstract EntityManager getEntityManager();

    @Override
    public <T> T persist(T entity) {
        getEntityManager().persist(entity);
        return entity;
    }

    @Override
    public <T> List<T> persist(T... entities) {
        List<T> result = new ArrayList<T>();
        for (T t : entities) {
            result.add(persist(t));
        }
        return result;
    }

    @Override
    public <T> T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public <T> List<T> merge(T... entities) {
        List<T> result = new ArrayList<T>();
        for (T t : entities) {
            result.add(merge(t));
        }
        return result;
    }

    @Override
    public void remove(Object entity) {
        EntityManager em = getEntityManager();
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public void remove(Object... entity) {
        for (Object t : entity) {
            remove(t);
        }
    }

    @Override
    public <T> T remove(Class<T> entityClass, Object ID) {
        T entity = findOne(entityClass, ID);
        remove(entity);
        return entity;
    }

    @Override
    public <T> List<T> remove(Class<T> entityClass, Object... ID) {
        List<T> result = new ArrayList<T>();
        for (Object id : ID) {
            result.add(remove(entityClass, id));
        }
        return result;
    }

    @Override
    public int removeAll(Class<?> entityClass) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete critDel = cb.createCriteriaDelete(entityClass);
        critDel.from(entityClass);
        return em.createQuery(critDel).executeUpdate();
    }

    @Override
    public <T> T findOne(Class<T> entityClass, Object ID) {
        return getEntityManager().find(entityClass, ID);
    }

    @Override
    public <T> T findOne(String jpql) {
        return (T) queryOne(getEntityManager().createQuery(jpql), (Object[]) null);
    }

    @Override
    public <T> T findOne(String jpql, Object... parameters) {
        return (T) queryOne(getEntityManager().createQuery(jpql), parameters);
    }

    @Override
    public <T> T findOne(String jpql, Map<String, Object> parameters) {
        return (T) queryOne(getEntityManager().createQuery(jpql), parameters);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return findAll(entityClass, null);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, Sort sort) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, cb));
        }
        return em.createQuery(query).getResultList();
    }

    @Override
    public <T> List<T> findAll(String jpql) {
        return getEntityManager().createQuery(jpql).getResultList();
    }

    @Override
    public <T> List<T> findAll(String jpql, Object... parameters) {
        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getResultList();
    }

    @Override
    public <T> List<T> findAll(String jpql, Map<String, Object> parameters) {
        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getResultList();
    }

    @Override
    public long countAll(Class<?> entityClass) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        query.select(builder.function("count", Long.class, query.from(entityClass)));
        return getEntityManager().createQuery(query).getSingleResult();
    }

    @Override
    public long count(String jpql) {
        return ((Number) getEntityManager().createQuery(jpql).getSingleResult()).longValue();
    }

    @Override
    public long count(String jpql, Object... parameters) {
        return ((Number) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult()).longValue();
    }

    @Override
    public long count(String jpql, Map<String, Object> parameters) {
        return ((Number) applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).getSingleResult()).longValue();
    }

    @Override
    public int executeUpdate(String jpql, Object... parameters) {
        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).executeUpdate();
    }

    @Override
    public int executeUpdate(String jpql, Map<String, Object> parameters) {
        return applyParameterToQuery(getEntityManager().createQuery(jpql), parameters).executeUpdate();
    }

    @Override
    public <T> T findOneBySQL(Class<T> requireType, String sql) {
        return (T) queryOne(getEntityManager().createNativeQuery(sql, requireType), (Object[]) null);
    }

    @Override
    public <T> T findOneBySQL(Class<T> requireType, String sql, Object... parameters) {
        return (T) queryOne(getEntityManager().createNativeQuery(sql, requireType), parameters);
    }

    @Override
    public <T> T findOneBySQL(Class<T> requireType, String sql, Map<String, Object> parameters) {
        return (T) queryOne(getEntityManager().createNativeQuery(sql, requireType), parameters);
    }

    @Override
    public <T> List<T> findAllBySQL(Class<T> requireType, String sql) {
        return getEntityManager().createNativeQuery(sql, requireType).getResultList();
    }

    @Override
    public <T> List<T> findAllBySQL(Class<T> requireType, String sql, Object... parameters) {
        return applyParameterToQuery(getEntityManager().createNativeQuery(sql, requireType), parameters).getResultList();
    }

    @Override
    public <T> List<T> findAllBySQL(Class<T> requireType, String sql, Map<String, Object> parameters) {
        return applyParameterToQuery(getEntityManager().createNativeQuery(sql, requireType), parameters).getResultList();
    }

    @Override
    public long countBySQL(String sql) {
        return ((Number) getEntityManager().createNativeQuery(sql).getSingleResult()).longValue();
    }

    @Override
    public long countBySQL(String sql, Object... parameters) {
        return ((Number) getEntityManager().createNativeQuery(sql).getSingleResult()).longValue();
    }

    @Override
    public long countBySQL(String sql, Map<String, Object> parameters) {
        return ((Number) getEntityManager().createNativeQuery(sql).getSingleResult()).longValue();
    }

    @Override
    public int executeUpdateBySQL(String sql, Object... parameters) {
        return applyParameterToQuery(getEntityManager().createNativeQuery(sql), parameters).executeUpdate();
    }

    @Override
    public int executeUpdateBySQL(String sql, Map<String, Object> parameters) {
        return applyParameterToQuery(getEntityManager().createNativeQuery(sql), parameters).executeUpdate();
    }

    private Object queryOne(Query query, Object[] parameters) {
        try {
            return applyParameterToQuery(query, parameters).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private Object queryOne(Query query, Map<String, Object> parameters) {
        try {
            return applyParameterToQuery(query, parameters).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    protected <T extends Query> T applyParameterToQuery(T query, Object[] parameters) {
        if (parameters != null && parameters.length > 0) {
            for (int i = 0, max = parameters.length; i < max; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
        }
        return query;
    }

    protected <T extends Query> T applyParameterToQuery(T query, Map<String, Object> parameters) {
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

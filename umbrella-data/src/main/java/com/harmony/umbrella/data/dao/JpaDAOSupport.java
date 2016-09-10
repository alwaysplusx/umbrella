package com.harmony.umbrella.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.util.JpaQueryBuilder;

/**
 * @author wuxii@foxmail.com
 */
public abstract class JpaDAOSupport extends DAOSupport implements JpaDAO {

    @Override
    public <T> int remove(Class<T> entityClass, Specification<T> spec) {
        EntityManager em = getEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaDelete<T> critDel = builder.createCriteriaDelete(entityClass);
        Root<T> root = critDel.from(entityClass);
        Predicate predicate = spec.toPredicate(root, null, builder);
        if (predicate != null) {
            critDel.where(predicate);
        }
        return em.createQuery(critDel).executeUpdate();
    }

    @Override
    public <T> T findOne(Class<T> entityClass, Specification<T> spec) {
        return query(entityClass).withSpecification(spec).getSingleResult();
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, Specification<T> spec) {
        return query(entityClass).withSpecification(spec).getResultList();
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, Specification<T> spec, Sort sort) {
        return query(entityClass).withSort(sort).withSpecification(spec).getResultList();
    }

    @Override
    public <T> Page<T> findAll(Class<T> entityClass, Pageable pageable) {
        return query(entityClass).withPageable(pageable).getResultPage();
    }

    @Override
    public <T> Page<T> findAll(Class<T> entityClass, Pageable pageable, Specification<T> spec) {
        return query(entityClass).withPageable(pageable).withSpecification(spec).getResultPage();
    }

    @Override
    public <T> long count(Class<T> entityClass, Specification<T> spec) {
        return query(entityClass).withSpecification(spec).count();
    }

    protected <M> JpaQueryBuilder<M> query(Class<M> entityClass) {
        return new JpaQueryBuilder<M>(getEntityManager()).withEntityClass(entityClass);
    }
}

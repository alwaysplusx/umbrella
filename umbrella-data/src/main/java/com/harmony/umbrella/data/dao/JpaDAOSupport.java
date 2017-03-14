package com.harmony.umbrella.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.JpaQueryBuilder;

/**
 * @deprecated spring-data repository or
 *             {@linkplain com.harmony.umbrella.data.repository.QueryableRepository}
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
        return queryWith(entityClass).withSpecification(spec).getSingleResult();
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, Specification<T> spec) {
        return queryWith(entityClass).withSpecification(spec).getResultList();
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, Specification<T> spec, Sort sort) {
        return queryWith(entityClass).sort(sort).withSpecification(spec).getResultList();
    }

    @Override
    public <T> Page<T> findAll(Class<T> entityClass, Pageable pageable) {
        return queryWith(entityClass).withPageable(pageable).getResultPage();
    }

    @Override
    public <T> Page<T> findAll(Class<T> entityClass, Pageable pageable, Specification<T> spec) {
        return queryWith(entityClass).withPageable(pageable).withSpecification(spec).getResultPage();
    }

    @Override
    public <T> long count(Class<T> entityClass, Specification<T> spec) {
        return queryWith(entityClass).withSpecification(spec).getCountResult();
    }

    protected <M> JpaQueryBuilder<M> queryWith(Class<M> entityClass) {
        return new JpaQueryBuilder<M>(getEntityManager()).from(entityClass);
    }

}

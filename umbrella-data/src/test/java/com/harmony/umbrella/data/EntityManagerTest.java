package com.harmony.umbrella.data;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;

import com.harmony.umbrella.data.entity.Model;

/**
 * @author wuxii@foxmail.com
 */
public class EntityManagerTest {

    static EntityManager entityManager = Persistence.createEntityManagerFactory("umbrella").createEntityManager();

    @Test
    public void testSize() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Model> query = builder.createQuery(Model.class);
        Root<Model> root = query.from(Model.class);
        Path path = root.get("subModels");
        Expression sizeExp = builder.size(path);
        Predicate predicate = builder.equal(sizeExp, 1);
        query.where(predicate);
        entityManager.createQuery(query).getResultList();
    }

}

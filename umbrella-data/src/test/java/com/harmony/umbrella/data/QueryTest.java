package com.harmony.umbrella.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.Test;

import com.harmony.umbrella.data.entity.Model;
import com.harmony.umbrella.data.util.QueryBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class QueryTest {

    static EntityManager entityManager = Persistence.createEntityManagerFactory("hibernate-umbrella").createEntityManager();
    static EntityManager eclipseEntityManager = Persistence.createEntityManagerFactory("eclipse-umbrella").createEntityManager();
    static EntityManager hibernateEntityManager = Persistence.createEntityManagerFactory("hibernate-umbrella").createEntityManager();

    @Test
    public void testQuery() {
        QueryBuilder builder = new QueryBuilder(eclipseEntityManager)//
                .withEntityClass(Model.class)//
                .fetch("subModels");//
        //.equal("id", 12);//
        List result = builder.getResultList();
        for (Object o : result) {
            System.out.println(o);
        }
    }

}

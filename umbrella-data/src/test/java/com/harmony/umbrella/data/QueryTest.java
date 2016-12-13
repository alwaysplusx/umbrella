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

    static EntityManager entityManager = Persistence.createEntityManagerFactory("umbrella").createEntityManager();

    @Test
    public void testQuery() {
        QueryBuilder builder = new QueryBuilder(entityManager)//
                .from(Model.class)//
                .equal("name", "1")//
                .orEqual("code", "abc123")//
                .start()//
                .equal("name", "2")//
                .orEqual("code", "def456")//
                .end();
        // .equal("id", 12);//
        List result = builder.getResultList();
        for (Object o : result) {
            System.out.println(o);
        }
    }

    @Test
    public void testExpresion() {
        QueryBuilder builder = new QueryBuilder(entityManager)//
                .from(Model.class)//
                .andExpression("name", "code", Operator.EQUAL);
        builder.getResultList();
    }

}

package com.harmony.umbrella.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.data.entity.Model;
import com.harmony.umbrella.data.entity.SubModel;
import com.harmony.umbrella.data.query.JpaQueryBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class ConditionTest {

    static EntityManager entityManager = Persistence.createEntityManagerFactory("umbrella").createEntityManager();

    private JpaQueryBuilder<Model> builder;

    @BeforeClass
    public static void beforeClass() {
        entityManager.getTransaction().begin();
        entityManager.persist(new Model(1l, "wuxii", "code1", "code1", new SubModel("sub1", "wuxii-sub1")));
        entityManager.persist(new Model(2l, "david", "code2", "c2"));
        entityManager.getTransaction().commit();
    }

    @Before
    public void before() {
        builder = new JpaQueryBuilder<Model>(Model.class, entityManager);
    }

    @Test
    public void testEqual() {
        assertEquals("code1", builder.equal("name", "wuxii").getSingleResult().getCode());
    }

    @Test
    public void testNotEqual() {
        assertEquals("code2", builder.notEqual("name", "wuxii").getSingleResult().getCode());
    }

    @Test
    public void testLike() {
        assertEquals("code1", builder.like("name", "wuxii").getSingleResult().getCode());
    }

    @Test
    public void testNotLike() {
        assertEquals("code2", builder.notLike("name", "wuxii").getSingleResult().getCode());
    }

    @Test
    public void testIn() {
        List<Long> ids = new ArrayList();
        for (long i = 2; i < 1001; i++) {
            ids.add(i);
        }
        ids.add(1l);
        builder.in("id", ids);
        List<Model> v = builder.getResultList();
        assertEquals(2, v.size());
    }

    @Test
    public void testNotIn() {
        List<Long> ids = new ArrayList();
        for (long i = 2; i < 1001; i++) {
            ids.add(i);
        }
        ids.add(1l);
        builder.notIn("id", ids);
        List<Model> v = builder.getResultList();
        assertEquals(0, v.size());
    }

    @Test
    public void testBetween() {
        assertEquals(2, builder.between("id", 1, 2).getCountResult());
    }

    @Test
    public void testNotBetween() {
        assertEquals(0, builder.notBetween("id", 1, 2).getCountResult());
    }

    @Test
    public void testGreatThen() {
        assertEquals(1, builder.greatThen("id", 1).getCountResult());
    }

    @Test
    public void testLessThen() {
        assertEquals(1, builder.lessThen("id", 2).getCountResult());
    }

    @Test
    public void testGreatEqual() {
        assertEquals(2, builder.greatEqual("id", 1).getCountResult());
    }

    @Test
    public void testLessEqual() {
        assertEquals(2, builder.lessEqual("id", 2).getCountResult());
    }

    @Test
    public void testIsNull() {
        assertEquals(2, builder.isNull("nullField").getCountResult());
    }

    @Test
    public void testNotNull() {
        assertEquals(0, builder.isNotNull("nullField").getCountResult());
    }

    @Test
    public void testSubquery() {
        JpaQueryBuilder<SubModel> builder = new JpaQueryBuilder<SubModel>(entityManager)//
                .from(SubModel.class)//
                .subquery(Model.class)//
                .select("id")//
                .equal("name", "wuxii")//
                .apply("model.id", Operator.IN);
        List<SubModel> v = builder.getResultList();
        assertEquals(1, v.size());
    }

}

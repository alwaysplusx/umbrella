package com.harmony.umbrella.data;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.domain.Page;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.data.entity.Model;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.JpaQueryBuilder.OneTimeColumn;
import com.harmony.umbrella.data.vo.ModelVo;

/**
 * @author wuxii@foxmail.com
 */
public class QueryTest {

    static EntityManager entityManager = Persistence.createEntityManagerFactory("umbrella").createEntityManager();

    private JpaQueryBuilder<Model> builder;

    @BeforeClass
    public static void beforeClass() {
        entityManager.getTransaction().begin();
        entityManager.persist(new Model(1l, "wuxii", "code1", "content"));
        entityManager.persist(new Model(2l, "david", "code2", "content"));
        entityManager.getTransaction().commit();
    }

    @Before
    public void before() {
        builder = new JpaQueryBuilder<Model>(Model.class, entityManager);
    }

    @Test
    public void testSingleResult() {
        Model v = builder//
                .equal("name", "wuxii")//
                .getSingleResult();
        assertEquals("code1", v.getCode());
    }

    @Test
    public void testResultList() {
        List<Model> v = builder//
                .equal("name", "wuxii")//
                .or()//
                .equal("name", "david")//
                .getResultList();
        assertEquals(2, v.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testFullTable() {
        builder.getResultList();
    }

    @Test
    public void testResultPage() {
        Page<Model> v = builder.paging(0, 20).getResultPage();
        System.out.println(JSON.toJSONString(v, SerializerFeature.PrettyFormat));
    }

    @Test
    public void testAsc() {
        List<Model> v = builder.asc("id").allowFullTableQuery().getResultList();
        assertEquals(1l, v.get(0).getId().longValue());
        assertEquals(2l, v.get(1).getId().longValue());
    }

    @Test
    public void testDesc() {
        List<Model> v = builder.desc("id").allowFullTableQuery().getResultList();
        assertEquals(2l, v.get(0).getId().longValue());
        assertEquals(1l, v.get(1).getId().longValue());
    }

    @Test
    public void testGrouping() {
        List<String> v = builder.allowFullTableQuery().groupBy("content").execute().getResultList("content", String.class);
        assertEquals(1, v.size());
    }

    @Test
    public void testFunctionQuery() {
        assertEquals(1l, builder.equal("name", "wuxii").execute().getFunctionResult("count", "name", Long.class).longValue());
    }

    @Test
    public void testVoQuery() {
        ModelVo v = builder.equal("name", "wuxii").execute().getSingleResult(new String[] { "name", "code", "content" }, ModelVo.class);
        assertEquals("wuxii", v.getName());
    }

    @Test
    public void testVoAndFunctionQuery() {
        List<ModelVo> v = builder.allowFullTableQuery().groupBy("content").execute().getResultList(new String[] { "max(id)", "content" }, ModelVo.class);
        assertEquals(1, v.size());
        assertEquals(2, v.get(0).getSize());
    }

    @Test
    public void testDistinctQuery() {
        List<String> v = builder.distinct().allowFullTableQuery().execute().getResultList("content", String.class);
        assertEquals(1, v.size());
    }

    @Test
    public void testSizeOf() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Model> query = cb.createQuery(Model.class);
        Root<Model> root = query.from(Model.class);
        Predicate predicate = cb.equal(cb.size(root.get("subModels")), 0);
        query.where(predicate);
        entityManager.createQuery(query).getResultList();
    }

    @Test
    public void testSizeOfWithBuilder() {
        builder.sizeOf("subModels", 0).getResultList();
    }

    @Test
    public void testSubModelQuery() {
        builder.equal("subModels.name", "wuxii").getResultList();
    }

    @Test
    public void testColumnQuery() {
        builder//
                .start("name").equal("wuxii")//
                .or("code").equal("1092")//
                .and()//
                .start("content").equal("abc")//
                .or("code").like("123")//
                .getResultList();
    }

    @Test
    public void testInnerClassInstance() {
        Object val = builder.start("name");
        System.out.println(val instanceof OneTimeColumn);
    }

}

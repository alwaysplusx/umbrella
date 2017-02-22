package com.harmony.umbrella.data;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.data.entity.Model;
import com.harmony.umbrella.data.entity.SubModel;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBuilder;
import com.harmony.umbrella.data.vo.ModelVo;

/**
 * @author wuxii@foxmail.com
 */
public class QueryTest {

    static EntityManager entityManager = Persistence.createEntityManagerFactory("umbrella").createEntityManager();

    @BeforeClass
    public static void beforeClass() {
        entityManager.getTransaction().begin();
        entityManager.persist(new Model(1l, "wuxii", "content"));
        entityManager.persist(new Model(2l, "aaa", "content"));
        entityManager.getTransaction().commit();
    }

    @Test
    public void testQuery() {
        QueryBuilder builder = new QueryBuilder(entityManager)//
                .from(Model.class)//
                .equal("name", "1")//
                .or()//
                .equal("code", "2")//
        ;
        builder.getResultList();
    }

    @Test
    public void testMergeCondition() {
        QueryBuilder builder = new QueryBuilder(entityManager)//
                .from(Model.class)//
                .equal("id", "4")//
                .start()//匹配括号开始
                .equal("name", "1")//
                .or()//
                .equal("code", "2")//
                .end()//匹配括号结束
        ;
        builder.getResultList();
    }

    @Test
    @Ignore
    public void testUntypeQuery() {
        QueryBuilder builder = new QueryBuilder(entityManager)//
                .from(Model.class)//
                .equal("createDate", "2014-09-01 21:20:23");// only support eclipselink
        builder.getResultList();
    }

    @Test
    public void testSubquery() {
        JpaQueryBuilder<SubModel> builder = new JpaQueryBuilder<SubModel>(entityManager)//
                .from(SubModel.class)//
                .subquery(Model.class)//
                .select("id")//
                .equal("name", "wuxii")//
                .apply("model.id", Operator.IN);
        builder.getResultList();
    }

    @Test
    public void testVoQuery() {
        JpaQueryBuilder<Model> builder = new JpaQueryBuilder<Model>(entityManager);
        builder.from(Model.class).equal("name", "wuxii");
        ModelVo vo = builder//
                .execute()//
                .getVoSingleResult(new String[] { "name", "code", "content" }, ModelVo.class);
        System.out.println(vo);
    }

    @Test
    public void testVoFunctionQuery() {
        JpaQueryBuilder<Model> builder = new JpaQueryBuilder<Model>(entityManager);
        builder.from(Model.class).equal("name", "wuxii");
        ModelVo vo = builder//
                .execute()//
                .getVoSingleResult(new String[] { "max(id)", "name", "content" }, ModelVo.class);
        System.out.println(vo);
    }

    @Test
    @Ignore
    public void persistenceXML() throws IOException {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("META-INF/persistence.xml");
        while (urls.hasMoreElements()) {
            System.out.println(urls.nextElement());
        }
    }

    @Test(expected = NullPointerException.class)
    public void testEmptySpec() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Model> query = builder.createQuery(Model.class);
        query.from(Model.class);
        query.where((Predicate) null);
        System.out.println(query.getRestriction().getExpressions().size());
        System.out.println(query.getGroupRestriction());
        entityManager.createQuery(query).getResultList();
    }

    @Test
    public void testGrouping() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Model> query = builder.createQuery(Model.class);
        Root<Model> root = query.from(Model.class);
        query.groupBy(root.get("name"));
        List<Model> list = entityManager.createQuery(query).getResultList();
        System.out.println(list);
    }

}

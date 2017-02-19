package com.harmony.umbrella.data;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.data.entity.Model;
import com.harmony.umbrella.data.entity.SubModel;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBuilder;

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
    @Ignore
    public void persistenceXML() throws IOException {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("META-INF/persistence.xml");
        while (urls.hasMoreElements()) {
            System.out.println(urls.nextElement());
        }
    }

}

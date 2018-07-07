package com.harmony.umbrella.data;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.h2.Driver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.Queryable.QueryResultConverter;
import com.harmony.umbrella.data.RepositoryTest.RepositoryConfig;
import com.harmony.umbrella.data.entity.Model;
import com.harmony.umbrella.data.entity.SubModel;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryBundle;
import com.harmony.umbrella.data.query.QueryFeature;
import com.harmony.umbrella.data.query.QueryResult;
import com.harmony.umbrella.data.repository.ModelRepository;
import com.harmony.umbrella.data.repository.support.QueryableRepositoryFactoryBean;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RepositoryConfig.class })
public class RepositoryTest {

    @Autowired
    private ModelRepository repository;

    private QueryBundle<Model> bundle;

    @Before
    public void before() {
        repository.save(new Model(1l, "wuxii", "code1", new SubModel("sub1", "wuxii-sub1")));
        repository.save(new Model(2l, "david", "code2"));
        bundle = new JpaQueryBuilder<>(Model.class).equal("name", "wuxii").bundle();
    }

    @Test
    public void test() {
        assertNotNull(repository);
        assertEquals("wuxii", repository.query(bundle).getSingleResult().getName());
    }

    @Test
    public void testFetchResult() {
        String name = repository.query(bundle, new QueryResultConverter<Model, String>() {

            @Override
            public String convert(QueryResult<Model> result) {
                return result.getSingleResult("name", String.class);
            }

        });
        assertEquals("wuxii", name);
    }

    @Test
    public void testRowQuery() {
        QueryBundle<SubModel> bundle = new JpaQueryBuilder<>(SubModel.class).enable(QueryFeature.FULL_TABLE_QUERY).bundle();
        List<SubModel> v = repository.query(bundle, SubModel.class).getResultList();
        assertEquals(1, v.size());
    }

    @Configuration
    @EnableJpaRepositories(repositoryFactoryBeanClass = QueryableRepositoryFactoryBean.class)
    public static class RepositoryConfig {

        @Bean
        DataSource dataSource() {
            return new SimpleDriverDataSource(Driver.load(), "jdbc:h2:file:~/.h2/harmony/data", "sa", "");
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean o = new LocalContainerEntityManagerFactoryBean();
            o.setDataSource(dataSource);
            o.setPersistenceUnitName("umbrella");
            o.setPersistenceXmlLocation("META-INF/persistence.xml");
            return o;
        }

        @Bean
        JpaTransactionManager transactionManager(EntityManagerFactory emf) {
            JpaTransactionManager o = new JpaTransactionManager();
            o.setEntityManagerFactory(emf);
            return o;
        }
    }

}

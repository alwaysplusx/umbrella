package com.harmony.umbrella.data.support;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * @author wuxii@foxmail.com
 */
public class QueryableRepositoryFactoryBean extends JpaRepositoryFactoryBean {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return super.createRepositoryFactory(entityManager);
    }

}

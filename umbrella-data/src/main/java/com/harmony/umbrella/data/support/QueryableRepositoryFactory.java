package com.harmony.umbrella.data.support;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

/**
 * @author wuxii@foxmail.com
 */
public class QueryableRepositoryFactory extends JpaRepositoryFactory {

    public QueryableRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public <T> T getRepository(Class<T> repositoryInterface, Object customImplementation) {
        return super.getRepository(repositoryInterface, customImplementation);
    }

}

package com.harmony.umbrella.data.repository.support;

import com.harmony.umbrella.data.repository.SimpleQueryableJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

/**
 * @author wuxii@foxmail.com
 */
public class QueryableRepositoryFactory extends JpaRepositoryFactory {

    public QueryableRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleQueryableJpaRepository.class;
    }

}

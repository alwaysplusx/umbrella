package com.harmony.umbrella.data.repository.support;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import com.harmony.umbrella.data.repository.SimpleQueryableJpaRepository;

/**
 * @author wuxii@foxmail.com
 */
public class QueryableRepositoryFactory extends JpaRepositoryFactory {

    public QueryableRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        return new SimpleQueryableJpaRepository<>(information.getDomainType(), entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleQueryableJpaRepository.class;
    }

}

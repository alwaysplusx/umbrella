package com.harmony.umbrella.data.repository;

import com.harmony.umbrella.data.QueryBundle;
import com.harmony.umbrella.data.Queryable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author wuxii@foxmail.com
 */
@NoRepositoryBean
public interface QueryableRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>, Queryable<T> {

    Optional<T> getSingleResult(QueryBundle<T> bundle);

    Optional<T> getFirstResult(QueryBundle<T> bundle);

    List<T> getListResult(QueryBundle<T> bundle);

    List<T> getAllResult(QueryBundle<T> bundle);

    Page<T> getPageResult(QueryBundle<T> bundle);

    long countResult(QueryBundle<T> bundle);

    EntityManager getEntityManager();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    Class<T> getDomainClass();

}

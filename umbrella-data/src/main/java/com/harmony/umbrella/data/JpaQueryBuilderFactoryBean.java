package com.harmony.umbrella.data;

import javax.persistence.EntityManager;

/**
 * @author wuxii
 */
public class JpaQueryBuilderFactoryBean {

    private EntityManager entityManager;

    private int pageNumber;
    private int pageSize;
    private QueryFeature[] queryFeatures;

    public JpaQueryBuilderFactoryBean(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <M> JpaQueryBuilder<M> newBuilder(Class<M> domainClass) {
        return newBuilder(domainClass, false);
    }

    public <M> JpaQueryBuilder<M> newBuilder(Class<M> domainClass, boolean applyDefaults) {
        JpaQueryBuilder<M> builder = JpaQueryBuilder.newBuilder(domainClass).withEntityManager(entityManager);
        if (applyDefaults) {
            builder.paging(pageNumber, pageSize).enable(queryFeatures);
        }
        return builder;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public QueryFeature[] getQueryFeatures() {
        return queryFeatures;
    }

    public void setQueryFeatures(QueryFeature[] queryFeatures) {
        this.queryFeatures = queryFeatures;
    }
}

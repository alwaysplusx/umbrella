package com.harmony.umbrella.data.config.support;

/**
 * @author wuxin
 */
public interface DataAuditingHandler<T> {

    void markCreated(T source);

    void markModified(T source);

}

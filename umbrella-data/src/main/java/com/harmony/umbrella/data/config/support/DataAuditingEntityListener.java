package com.harmony.umbrella.data.config.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author wuxin
 */
@Slf4j
@Configurable
public class DataAuditingEntityListener implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;
    private DataAuditingHandler<Object> dataAuditingHandler;

    public DataAuditingEntityListener() {
    }

    public DataAuditingEntityListener(DataAuditingHandler<Object> dataAuditingHandler) {
        this.dataAuditingHandler = dataAuditingHandler;
    }

    @PrePersist
    public void touchForCreate(Object target) {
        log.debug("auditing entity create, {}", target);
        if (dataAuditingHandler != null) {
            dataAuditingHandler.markCreated(target);
        }
    }

    @PreUpdate
    public void touchForUpdate(Object target) {
        log.debug("auditing entity update, {}", target);
        if (dataAuditingHandler != null) {
            dataAuditingHandler.markModified(target);
        }
    }

    public void setDataAuditingHandler(DataAuditingHandler<Object> dataAuditingHandler) {
        Assert.notNull(dataAuditingHandler, "DataAuditingHandler must not be null!");
        this.dataAuditingHandler = dataAuditingHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DataAuditingHandler> beans = applicationContext.getBeansOfType(DataAuditingHandler.class);
        this.dataAuditingHandler = new DataAuditingHandlerComposite(beans.values());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static class DataAuditingHandlerComposite implements DataAuditingHandler<Object> {

        private Collection<DataAuditingHandler> handlers;

        public DataAuditingHandlerComposite(Collection<DataAuditingHandler> handlers) {
            this.handlers = Collections.unmodifiableCollection(handlers);
        }

        @Override
        public void markCreated(Object source) {
            handlers.forEach(e -> e.markCreated(source));
        }

        @Override
        public void markModified(Object source) {
            handlers.forEach(e -> e.markModified(source));
        }

    }

}

package com.harmony.console.log.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.harmony.console.log.persistence.LogEntity;
import com.harmony.umbrella.data.dao.JpaDAO;
import com.harmony.umbrella.data.service.ServiceSupport;

/**
 * @author wuxii@foxmail.com
 */
@Service
public class LogServiceBean extends ServiceSupport<LogEntity, Long> implements LogService {

    @Inject
    private JpaDAO consoleDao;

    protected LogServiceBean() {
        super(LogEntity.class);
    }

    @Override
    protected JpaDAO getJpaDAO() {
        return consoleDao;
    }

}

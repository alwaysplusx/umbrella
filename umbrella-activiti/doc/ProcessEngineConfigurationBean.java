package com.harmony.umbrella.activiti.ee;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.CommandInterceptor;

/**
 * @author wuxii@foxmail.com
 */
public class ProcessEngineConfigurationBean extends ProcessEngineConfigurationImpl {

    @Override
    protected CommandInterceptor createTransactionInterceptor() {
        return null;
    }

    @Override
    @Resource
    public DataSource getDataSource() {
        return super.getDataSource();
    }

}

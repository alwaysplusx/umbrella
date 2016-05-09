package com.harmony.core.sys.biz;

import javax.ejb.Stateless;

import com.harmony.core.sys.entity.LogEntity;
import com.harmony.umbrella.biz.BusinessSupport;
import com.harmony.umbrella.data.JpaDao;

/**
 * @author wuxii@foxmail.com
 */
@Stateless
public class LogServiceBean extends BusinessSupport<LogEntity, String> implements LogService {

    @Override
    protected JpaDao<LogEntity, String> getJpaDao() {
        return null;
    }

}

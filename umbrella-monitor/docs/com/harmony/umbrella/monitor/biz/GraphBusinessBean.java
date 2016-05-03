package com.harmony.umbrella.monitor.biz;

import org.springframework.stereotype.Service;

import com.harmony.umbrella.biz.BusinessSupport;
import com.harmony.umbrella.data.JpaDao;
import com.harmony.umbrella.monitor.dao.GraphDao;
import com.harmony.umbrella.monitor.persistence.GraphEntity;

/**
 * @author wuxii@foxmail.com
 */
@Service
public class GraphBusinessBean extends BusinessSupport<GraphEntity, Long> implements GraphBusiness {

    private GraphDao graphDao;

    @Override
    protected JpaDao<GraphEntity, Long> getJpaDao() {
        return graphDao;
    }

}

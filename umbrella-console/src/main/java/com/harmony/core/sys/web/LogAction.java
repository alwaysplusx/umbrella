package com.harmony.core.sys.web;

import javax.ejb.EJB;

import com.harmony.core.sys.biz.LogService;
import com.harmony.core.sys.entity.LogEntity;
import com.harmony.umbrella.web.action.ModelAction;

/**
 * @author wuxii@foxmail.com
 */
public class LogAction extends ModelAction<LogEntity, String> {

    private static final long serialVersionUID = 1L;

    @EJB
    private LogService logService;

    public void model() {

    }

}

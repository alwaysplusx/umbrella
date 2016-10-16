package com.harmony.umbrella.context;

import javax.servlet.ServletException;

/**
 * @author wuxii@foxmail.com
 */
public interface ApplicationInitializer {

    void onStartup(ApplicationConfiguration appCfg) throws ServletException;

}

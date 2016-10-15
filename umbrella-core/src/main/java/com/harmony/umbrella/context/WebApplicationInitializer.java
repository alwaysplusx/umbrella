package com.harmony.umbrella.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author wuxii@foxmail.com
 */
public interface WebApplicationInitializer {

    void onStartup(ServletContext servletContext, ApplicationConfiguration applicationCfg) throws ServletException;

}

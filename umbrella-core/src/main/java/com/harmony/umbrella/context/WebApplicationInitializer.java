package com.harmony.umbrella.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author wuxii@foxmail.com
 */
public interface WebApplicationInitializer {

    void onStartup(ServletContext servletContext) throws ServletException;

}

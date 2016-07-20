package com.harmony.umbrella.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationInitializer;

/**
 * @author wuxii@foxmail.com
 */
public class WebApplicationContextListener implements ServletContextListener, HttpSessionListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new WebApplicationInitializer(sce.getServletContext()).init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ApplicationContext.getApplicationContext().destroy();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }

    private class WebApplicationInitializer extends ApplicationInitializer {

        public WebApplicationInitializer(ServletContext servletContext) {
            super(servletContext);
        }

        @Override
        protected String getInitParam(String name) {
            return super.getInitParam(name);
        }

        @Override
        protected String[] getInitParams(String name) {
            return null;
        }

    }

}

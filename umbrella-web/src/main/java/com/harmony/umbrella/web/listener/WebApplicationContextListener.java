package com.harmony.umbrella.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.harmony.umbrella.context.ApplicationContextListener;

/**
 * @author wuxii@foxmail.com
 */
public class WebApplicationContextListener extends ApplicationContextListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }

}

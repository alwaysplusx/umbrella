/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.bang.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author wuxii@foxmail.com
 */
public class WebApplicationListener extends ContextLoaderListener implements HttpSessionListener, ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(WebApplicationListener.class);

    private WebApplicationContext application;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ServletContext context = event.getServletContext();
        application = (WebApplicationContext) context.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        log.info("{}", application);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.info("created a new http session, session id={}", se.getSession().getId());
        log.info("application = {}", application);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.info("destoryed a http session, session id={}", se.getSession().getId());
    }

}

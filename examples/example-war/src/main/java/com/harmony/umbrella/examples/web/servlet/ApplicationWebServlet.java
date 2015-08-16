/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.examples.web.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.example.current.CurrentUserRemote;

/**
 * @author wuxii@foxmail.com
 */
@WebServlet(urlPatterns = { "/app" })
public class ApplicationWebServlet extends HttpServlet {

    private static final long serialVersionUID = -6186232461484475907L;

    @EJB
    private CurrentUserRemote currentUserBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log(">>>> current user " + currentUserBean.getUser());

        ApplicationContext context = ApplicationContext.getApplicationContext();
        CurrentUserRemote bean = context.getBean(CurrentUserRemote.class);
        System.out.println(bean);

    }

}

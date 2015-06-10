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
package com.harmony.example.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuxii@foxmail.com
 */
@WebServlet(urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            log.info("{} : {}", name, req.getHeader(name));
        }

        Subject subject = SecurityUtils.getSubject();
        
        log.info("current subject >> {}", subject);
        if (subject.isAuthenticated()) {
            log.info("already login");
            resp.getWriter().write("{\"success\":true, \"url\":\"index.jsp\"}");
            resp.getWriter().flush();
            // resp.sendRedirect("index.jsp");
        } else {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            if (username != null) {
                subject.login(token);
                resp.getWriter().write("{\"success\":true, \"url\":\"success.jsp\"}");
                resp.getWriter().flush();
            } else {
                resp.sendRedirect("login.jsp");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}

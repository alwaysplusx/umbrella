/*
 * Copyright 2014-2015 the original author or authors.
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
package com.harmony.bang.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.harmony.bang.user.UserManager;
import com.harmony.bang.user.biz.UserBusiness;
import com.harmony.bang.user.persistence.User;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.util.PropUtils;
import com.harmony.umbrella.web.render.WebRender;
import com.harmony.umbrella.web.util.FrontUtils;

/**
 * @author wuxii@foxmail.com、
 */
@Controller
public class IndexController {

    protected static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UserBusiness userBiz;

    private ApplicationContext appContext = ApplicationContext.getApplicationContext();

    private UserManager userManager = new UserManager();

    @RequestMapping("/")
    public String index() {
        CurrentContext cc = appContext.getCurrentContext();
        return cc != null && cc.isAuthenticated() ? "main" : "index";
    }

    @ResponseBody
    @RequestMapping(value = "login")
    public Map<String, Object> login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request) {
        userManager.login(new User(1l, username), request);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("success", true);
        model.put("message", "login success");
        return model;
    }

    @ResponseBody
    @RequestMapping("logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("success", true);
        model.put("message", "logout success");
        userManager.logout(request);
        return model;
    }

    @ResponseBody
    @RequestMapping("locale-{lang}.js")
    public void locale(@PathVariable("lang") String lang, HttpServletRequest request, HttpServletResponse response) {
        Properties props = new Properties();
        String messageLocation = "locale/Message_" + lang + ".properties";
        if (PropUtils.exists(messageLocation)) {
            props.putAll(PropUtils.loadProperties(messageLocation));
        }
        props.put("lang", lang);
        try {
            new WebRender().renderText("var locale = " + FrontUtils.toJson(props), response);
        } catch (IOException e) {
        }
    }

}

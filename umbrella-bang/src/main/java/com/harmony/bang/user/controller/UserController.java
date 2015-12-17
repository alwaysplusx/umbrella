/*
 * Copyright 2012-2015 the original author or authors.
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
package com.harmony.bang.user.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.harmony.bang.user.biz.UserBusiness;
import com.harmony.bang.user.persistence.User;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.web.front.spring.FrontJsonView;
import com.harmony.umbrella.web.util.FrontPage;
import com.harmony.umbrella.web.util.FrontRequest;

/**
 * @author wuxii@foxmail.com
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserBusiness userBiz;

    @RequestMapping("")
    public String index() {
        return "user/user_list";
    }

    @ResponseBody
    @RequestMapping("/list")
    public List<User> list() {
        return userBiz.findAll();
    }

    @ResponseBody
    @RequestMapping("/list2")
    public FrontJsonView list2() {
        return new FrontJsonView(list(), "username");
    }

    @ResponseBody
    @RequestMapping("/page")
    public FrontPage page(FrontRequest pageRequest) {
        Page<User> page = userBiz.page(pageRequest);
        return new FrontPage(page);
    }

    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Map<String, Object> save(@ModelAttribute User user) {
        Map<String, Object> model = new HashMap<String, Object>();
        if (user == null) {
            model.put("success", false);
            model.put("message", "please input user information");
        } else {
            user = userBiz.save(user);
            model.put("success", true);
            model.put("id", user.getUserId());
        }
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Map<String, Object> update(User user) {
        Map<String, Object> model = new HashMap<String, Object>();
        if (user == null) {
            model.put("success", false);
            model.put("message", "please input user information");
        } else {
            user = userBiz.update(user);
            model.put("success", true);
            model.put("id", user.getUserId());
        }
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.GET)
    public Map<String, Object> delete(@PathVariable("userId") Long userId) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = userBiz.deleteById(userId);
        model.put("success", true);
        model.put("data", user);
        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Map<String, Object> delete(Long[] ids) {
        Map<String, Object> model = new HashMap<String, Object>();
        userBiz.deleteByIds(Arrays.asList(ids));
        model.put("success", true);
        return model;
    }

}

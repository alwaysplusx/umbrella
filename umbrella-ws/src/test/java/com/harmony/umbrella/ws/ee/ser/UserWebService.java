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
package com.harmony.umbrella.ws.ee.ser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.harmony.umbrella.ws.ee.User;
import com.harmony.umbrella.ws.ee.UserService;
import com.harmony.umbrella.ws.ser.Message;
import com.harmony.umbrella.ws.ser.MessageContent;
import com.harmony.umbrella.ws.ser.ServerSupport;

/**
 * @author wuxii@foxmail.com
 */
@WebService(serviceName = "UserService", targetNamespace = "http://www.umbrella.com/user")
public class UserWebService extends ServerSupport implements UserService {

    private final List<User> users = new ArrayList<User>();

    public UserWebService() {
        this.extract = false;
    }

    @Override
    public Message accessUser(String type, User user) {
        if ("S".equals(type)) {
            return saveUser(user);
        } else if ("U".equals(type)) {
            return updateUser(user);
        } else if ("D".equals(type)) {
            return deleteUser(user.getName());
        }
        return error("不支持的操作类型");
    }

    public List<User> findUser(@WebParam(name = "name") String name) {
        List<User> result = new ArrayList<User>();
        for (User user : users) {
            if (name != null && name.equals(user.getName())) {
                result.add(user);
            }
        }
        return result;
    }

    @WebMethod(exclude = true)
    public Message saveUser(@WebParam(name = "user") User user) {
        MessageContent content = createContent();
        if (isValid(user, content)) {
            return error(content);
        }
        users.add(user);
        content.append(getKey(user), "save success!");
        return success(content);
    }

    @WebMethod(exclude = true)
    public Message updateUser(User user) {
        MessageContent content = createContent();
        if (isValid(user, content)) {
            return error(content);
        }
        for (User u : users) {
            if (u.getName().equals(user.getName())) {
                u.setAge(user.getAge());
            }
        }
        content.put(getKey(user), "update success!");
        return success(content);
    }

    @WebMethod(exclude = true)
    public Message deleteUser(String name) {
        MessageContent content = createContent();
        if (isValid(name, content)) {
            return error(content);
        }
        Iterator<User> uit = users.iterator();
        while (uit.hasNext()) {
            User u = uit.next();
            if (u.getName().equals(name)) {
                uit.remove();
            }
        }
        content.append(name, "delete success!");
        return success();
    }

}
